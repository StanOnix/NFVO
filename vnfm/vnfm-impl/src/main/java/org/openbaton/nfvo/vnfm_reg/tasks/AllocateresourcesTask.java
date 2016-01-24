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

package org.openbaton.nfvo.vnfm_reg.tasks;

import org.openbaton.catalogue.mano.common.Event;
import org.openbaton.catalogue.mano.common.LifecycleEvent;
import org.openbaton.catalogue.mano.descriptor.VirtualDeploymentUnit;
import org.openbaton.catalogue.mano.record.Status;
import org.openbaton.catalogue.mano.record.VNFCInstance;
import org.openbaton.catalogue.nfvo.Action;
import org.openbaton.catalogue.nfvo.messages.Interfaces.NFVMessage;
import org.openbaton.catalogue.nfvo.messages.OrVnfmErrorMessage;
import org.openbaton.catalogue.nfvo.messages.OrVnfmGenericMessage;
import org.openbaton.exceptions.VimException;
import org.openbaton.nfvo.core.interfaces.ResourceManagement;
import org.openbaton.nfvo.vnfm_reg.tasks.abstracts.AbstractTask;
import org.openbaton.exceptions.VimDriverException;
import org.openbaton.vnfm.interfaces.sender.VnfmSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lto on 06/08/15.
 */
@Service
@Scope("prototype")
public class AllocateresourcesTask extends AbstractTask {
    @Autowired
    private ResourceManagement resourceManagement;

    @Override
    protected NFVMessage doWork() throws Exception {
        VnfmSender vnfmSender;
        vnfmSender = this.getVnfmSender(vnfmRegister.getVnfm(virtualNetworkFunctionRecord.getEndpoint()).getEndpointType());

        log.debug("NFVO: ALLOCATE_RESOURCES");
        log.debug("Verison is: " + virtualNetworkFunctionRecord.getHb_version());
        for (VirtualDeploymentUnit vdu : virtualNetworkFunctionRecord.getVdu()) {
            try {
                List<String> extIds = resourceManagement.allocate(vdu, virtualNetworkFunctionRecord);
                log.debug("the returned ext id is: " + extIds);
            } catch (VimException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                LifecycleEvent lifecycleEvent = new LifecycleEvent();
                lifecycleEvent.setEvent(Event.ERROR);
                VNFCInstance vnfcInstance = e.getVnfcInstance();

                log.debug("\n" + vnfcInstance);
                if (vnfcInstance != null)
                    vdu.getVnfc_instance().add(vnfcInstance);
                virtualNetworkFunctionRecord.getLifecycle_event_history().add(lifecycleEvent);
                saveVirtualNetworkFunctionRecord();
                OrVnfmErrorMessage nfvMessage = new OrVnfmErrorMessage(virtualNetworkFunctionRecord, e.getMessage());
                return nfvMessage;
            } catch (VimDriverException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                LifecycleEvent lifecycleEvent = new LifecycleEvent();
                lifecycleEvent.setEvent(Event.ERROR);
                virtualNetworkFunctionRecord.getLifecycle_event_history().add(lifecycleEvent);
                virtualNetworkFunctionRecord.setStatus(Status.ERROR);
                saveVirtualNetworkFunctionRecord();
                OrVnfmErrorMessage nfvMessage = new OrVnfmErrorMessage(virtualNetworkFunctionRecord, e.getMessage());
                return nfvMessage;
            }
        }

        for (LifecycleEvent event : virtualNetworkFunctionRecord.getLifecycle_event()) {
            if (event.getEvent().ordinal() == Event.ALLOCATE.ordinal()) {
                virtualNetworkFunctionRecord.getLifecycle_event_history().add(event);
                break;
            }
        }
        saveVirtualNetworkFunctionRecord();

        OrVnfmGenericMessage orVnfmGenericMessage = new OrVnfmGenericMessage(virtualNetworkFunctionRecord, Action.ALLOCATE_RESOURCES);
        log.debug("Answering to RPC allocate resources: " + orVnfmGenericMessage);
        return orVnfmGenericMessage;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
