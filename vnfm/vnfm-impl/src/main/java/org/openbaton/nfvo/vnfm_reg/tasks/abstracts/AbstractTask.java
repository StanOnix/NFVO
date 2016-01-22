/*
 * Copyright (c) 2015 Fraunhofer FOKUS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openbaton.nfvo.vnfm_reg.tasks.abstracts;

import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.mano.record.Status;
import org.openbaton.catalogue.mano.record.VNFRecordDependency;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.catalogue.nfvo.Action;
import org.openbaton.catalogue.nfvo.ApplicationEventNFVO;
import org.openbaton.catalogue.nfvo.EndpointType;
import org.openbaton.catalogue.nfvo.messages.Interfaces.NFVMessage;
import org.openbaton.catalogue.nfvo.messages.OrVnfmErrorMessage;
import org.openbaton.catalogue.util.EventFinishEvent;
import org.openbaton.exceptions.NotFoundException;
import org.openbaton.nfvo.common.internal.model.EventFinishNFVO;
import org.openbaton.nfvo.common.internal.model.EventNFVO;
import org.openbaton.nfvo.repositories.NetworkServiceRecordRepository;
import org.openbaton.nfvo.repositories.VNFRRepository;
import org.openbaton.nfvo.vnfm_reg.VnfmRegister;
import org.openbaton.vnfm.interfaces.manager.VnfmManager;
import org.openbaton.vnfm.interfaces.sender.VnfmSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by lto on 06/08/15.
 */

/**
 * Putting these annotations only here won't work.
 */
@Service
@Scope("prototype")
public abstract class AbstractTask implements Callable<NFVMessage>, ApplicationEventPublisherAware {
    protected Logger log = LoggerFactory.getLogger(AbstractTask.class);
    protected Action action;
    @Autowired
    @Qualifier("vnfmRegister")
    protected VnfmRegister vnfmRegister;
    protected VirtualNetworkFunctionRecord virtualNetworkFunctionRecord;
    protected VNFRecordDependency dependency;
    @Autowired
    protected VNFRRepository vnfrRepository;
    @Autowired
    protected VnfmManager vnfmManager;
    @Autowired
    protected NetworkServiceRecordRepository networkServiceRecordRepository;
    @Autowired
    private ConfigurableApplicationContext context;
    private ApplicationEventPublisher publisher;

    @Transactional
    protected synchronized void saveVirtualNetworkFunctionRecord() {
        log.trace("ACTION is: " + action + " and the VNFR id is: " + virtualNetworkFunctionRecord.getId());
        if (virtualNetworkFunctionRecord.getId() == null) {
            virtualNetworkFunctionRecord = networkServiceRecordRepository.addVnfr(virtualNetworkFunctionRecord, virtualNetworkFunctionRecord.getParent_ns_id());
        } else {
            virtualNetworkFunctionRecord = vnfrRepository.save(virtualNetworkFunctionRecord);
        }

    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public VirtualNetworkFunctionRecord getVirtualNetworkFunctionRecord() {
        return virtualNetworkFunctionRecord;
    }

    public void setVirtualNetworkFunctionRecord(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) {
        this.virtualNetworkFunctionRecord = virtualNetworkFunctionRecord;
    }

    @Override
    public NFVMessage call() {
        changeStatus();
        NFVMessage result = null;
        try {
            result = this.doWork();
        } catch (Exception e) {
            VnfmSender vnfmSender;
            try {
                vnfmSender = this.getVnfmSender(vnfmRegister.getVnfm(virtualNetworkFunctionRecord.getEndpoint()).getEndpointType());
            } catch (NotFoundException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
            NFVMessage message = new OrVnfmErrorMessage(virtualNetworkFunctionRecord, e.getMessage());
            try {
                vnfmSender.sendCommand(message, vnfmRegister.getVnfm(virtualNetworkFunctionRecord.getEndpoint()));
            } catch (NotFoundException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
            if (log.isDebugEnabled()) {
                log.error("There was an uncaught exception in task: " + virtualNetworkFunctionRecord.getTask() + ". ", e);
            } else {
                log.error("There was an uncaught exception. Message is: " + e.getMessage());
            }

            EventFinishEvent eventFinishEvent = new EventFinishEvent();
            eventFinishEvent.setAction(Action.ERROR);
            virtualNetworkFunctionRecord.setStatus(Status.ERROR);
            eventFinishEvent.setVirtualNetworkFunctionRecord(virtualNetworkFunctionRecord);
            EventFinishNFVO event = new EventFinishNFVO(this);
            event.setEventNFVO(eventFinishEvent);
            this.publisher.publishEvent(event);
        }
        /**
         * Send event finish
         */
        if (result == null) {
            if ((action.ordinal() != Action.ALLOCATE_RESOURCES.ordinal()) && (action.ordinal() != Action.GRANT_OPERATION.ordinal())) {
                vnfmManager.findAndSetNSRStatus(virtualNetworkFunctionRecord);
            }
            ApplicationEventNFVO eventPublic = new ApplicationEventNFVO(action, virtualNetworkFunctionRecord);
            EventNFVO eventNFVO = new EventNFVO(this);
            eventNFVO.setEventNFVO(eventPublic);
            log.debug("Publishing event: " + eventPublic);
            publisher.publishEvent(eventNFVO);
            return null;
        } else
            return result;
    }

    protected abstract NFVMessage doWork() throws Exception;

    public boolean isAsync() {
        return true;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    protected VnfmSender getVnfmSender(EndpointType endpointType) throws BeansException {
        String senderName = endpointType.toString().toLowerCase() + "VnfmSender";
        return (VnfmSender) this.context.getBean(senderName);
    }

    protected void changeStatus() {
        log.debug("Action is: " + action);
        Status status = virtualNetworkFunctionRecord.getStatus();
        switch (action) {
            case ALLOCATE_RESOURCES:
                status = Status.NULL;
                break;
            case SCALE_IN:
                break;
            case SCALING:
                status = Status.SCALING;
                break;
            case ERROR:
                status = Status.ERROR;
                break;
            case MODIFY:
                status = Status.INACTIVE;
                break;
            case RELEASE_RESOURCES:
                status = Status.TERMINATED;
                break;
            case HEAL:
                status = Status.ACTIVE;
                break;
            case GRANT_OPERATION:
                status = Status.NULL;
                break;
            case INSTANTIATE:
                status = Status.INITIALIZED;
                break;
            case SCALED:
                status = Status.ACTIVE;
                break;
            case RELEASE_RESOURCES_FINISH:
                status = Status.TERMINATED;
                break;
            case INSTANTIATE_FINISH:
                status = Status.ACTIVE;
                break;
            case CONFIGURE:
                break;
            case START:
                status = Status.ACTIVE;
                break;
        }
        virtualNetworkFunctionRecord.setStatus(status);
        log.debug("Changing status of VNFR: " + virtualNetworkFunctionRecord.getName() + " ( " + virtualNetworkFunctionRecord.getId() + " ) to " + status);
    }

    public void setDependency(VNFRecordDependency dependency) {
        this.dependency = dependency;
    }

    protected VirtualNetworkFunctionRecord getNextToCallStart(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) {
        Map<String, Integer> vnfrNames = vnfmManager.getVnfrNames().get(virtualNetworkFunctionRecord.getParent_ns_id());

        log.debug("List of VNFRs to start: " + vnfrNames);

        if (vnfrNames.size() > 0)
            for (Map.Entry<String, Integer> entry : vnfrNames.entrySet()) {
                vnfrNames.remove(entry.getKey());
                for (VirtualNetworkFunctionRecord vnfr : networkServiceRecordRepository.findFirstById(virtualNetworkFunctionRecord.getParent_ns_id()).getVnfr()) {
                    if (vnfr.getName().equals(entry.getKey())) {
                        return vnfr;
                    }
                }

                return null;
            }

        return null;
    }

    protected boolean allVnfrInInactive(NetworkServiceRecord nsr) {
        for (VirtualNetworkFunctionRecord virtualNetworkFunctionRecord : nsr.getVnfr())
            if (virtualNetworkFunctionRecord.getStatus().ordinal() < Status.INACTIVE.ordinal()) {
                log.debug("VNFR " + virtualNetworkFunctionRecord.getName() + " is in state: " + virtualNetworkFunctionRecord.getStatus());
                return false;
            }
        return true;
    }
}
