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

package org.openbaton.nfvo.vnfm_reg;

import com.google.gson.Gson;
import org.openbaton.catalogue.mano.common.VNFDeploymentFlavour;
import org.openbaton.catalogue.mano.descriptor.*;
import org.openbaton.catalogue.mano.record.*;
import org.openbaton.catalogue.nfvo.*;
import org.openbaton.catalogue.nfvo.messages.Interfaces.NFVMessage;
import org.openbaton.catalogue.nfvo.messages.*;
import org.openbaton.exceptions.NotFoundException;
import org.openbaton.exceptions.VimException;
import org.openbaton.nfvo.common.internal.model.EventFinishNFVO;
import org.openbaton.nfvo.common.internal.model.EventNFVO;
import org.openbaton.nfvo.core.interfaces.ConfigurationManagement;
import org.openbaton.nfvo.repositories.NetworkServiceDescriptorRepository;
import org.openbaton.nfvo.repositories.NetworkServiceRecordRepository;
import org.openbaton.nfvo.repositories.VimRepository;
import org.openbaton.nfvo.repositories.VnfPackageRepository;
import org.openbaton.nfvo.vnfm_reg.tasks.ScaledTask;
import org.openbaton.nfvo.vnfm_reg.tasks.abstracts.AbstractTask;
import org.openbaton.vnfm.interfaces.sender.VnfmSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by lto on 08/07/15.
 */
@Service
@Scope
@Order(value = (Ordered.LOWEST_PRECEDENCE - 10)) // in order to be the second to last
@ConfigurationProperties
public class VnfmManager implements org.openbaton.vnfm.interfaces.manager.VnfmManager, ApplicationEventPublisherAware, ApplicationListener<EventFinishNFVO>, CommandLineRunner {
    private static Map<String, Map<String, Integer>> vnfrNames;
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    @Qualifier("vnfmRegister")
    private VnfmRegister vnfmRegister;
    private ApplicationEventPublisher publisher;
    private ThreadPoolTaskExecutor asyncExecutor;
    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ConfigurationManagement configurationManagement;
    @Autowired
    private NetworkServiceRecordRepository nsrRepository;
    @Autowired
    private NetworkServiceDescriptorRepository nsdRepository;
    @Autowired
    private VnfPackageRepository vnfPackageRepository;
    @Autowired
    private VimRepository vimInstanceRepository;
    @Autowired
    private Gson gson;
    @Value("${nfvo.start.ordered:")
    private String ordered;

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public void init() {

        vnfrNames = new LinkedHashMap<>();
        /**
         * Asynchronous thread executor configuration
         */
        Configuration system;
        try {
            system = configurationManagement.queryByName("system");
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        this.asyncExecutor = new ThreadPoolTaskExecutor();

        this.asyncExecutor.setThreadNamePrefix("OpenbatonTask-");

        int maxPoolSize = 0;
        int corePoolSize = 0;
        int queueCapacity = 0;
        int keepAliveSeconds = 0;

        for (ConfigurationParameter configurationParameter : system.getConfigurationParameters()) {
            if (configurationParameter.getConfKey().equals("vmanager-executor-max-pool-size")) {
                maxPoolSize = Integer.parseInt(configurationParameter.getValue());
            }
            if (configurationParameter.getConfKey().equals("vmanager-executor-core-pool-size")) {
                corePoolSize = Integer.parseInt(configurationParameter.getValue());
            }
            if (configurationParameter.getConfKey().equals("vmanager-executor-queue-capacity")) {
                queueCapacity = Integer.parseInt(configurationParameter.getValue());
            }
            if (configurationParameter.getConfKey().equals("vmanager-keep-alive")) {
                keepAliveSeconds = Integer.parseInt(configurationParameter.getValue());
            }

        }

        if (maxPoolSize != 0) {
            this.asyncExecutor.setMaxPoolSize(maxPoolSize);
        } else {
            this.asyncExecutor.setMaxPoolSize(30);
        }
        if (corePoolSize != 0) {
            this.asyncExecutor.setCorePoolSize(corePoolSize);
        } else {
            this.asyncExecutor.setCorePoolSize(5);
        }

        if (queueCapacity != 0) {
            this.asyncExecutor.setQueueCapacity(queueCapacity);
        } else {
            this.asyncExecutor.setQueueCapacity(0);
        }
        if (keepAliveSeconds != 0) {
            this.asyncExecutor.setKeepAliveSeconds(keepAliveSeconds);
        } else {
            this.asyncExecutor.setKeepAliveSeconds(20);
        }


        this.asyncExecutor.initialize();

        log.trace("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        log.debug("ThreadPollTaskExecutor configuration:");
        log.debug("MaxPoolSize = " + this.asyncExecutor.getMaxPoolSize());
        log.debug("CorePoolSize = " + this.asyncExecutor.getCorePoolSize());
        log.debug("QueueCapacity = " + this.asyncExecutor.getThreadPoolExecutor().getQueue().size());
        log.debug("KeepAlive = " + this.asyncExecutor.getKeepAliveSeconds());
        log.trace("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }

    @Override
    @Async
    public Future<Void> deploy(NetworkServiceDescriptor networkServiceDescriptor, NetworkServiceRecord networkServiceRecord) throws NotFoundException {

        // fill up the vnfr
        Set<VNFRecordDependency> dependencies = networkServiceRecord.getVnf_dependency();

        vnfrNames.put(networkServiceRecord.getId(), new HashMap<String, Integer>());

        Map<String, Integer> vnfrNamesWeighted = vnfrNames.get(networkServiceRecord.getId());

        // calculate dependencies
        if (ordered != null && Boolean.parseBoolean(ordered))
            fillVnfrNames(networkServiceDescriptor, dependencies, vnfrNamesWeighted);

        vnfrNames.put(networkServiceRecord.getId(), sortByValue(vnfrNamesWeighted));

        log.debug("VNFRs ordered by depednencies: " + vnfrNamesWeighted);

        for (VirtualNetworkFunctionDescriptor vnfd : networkServiceDescriptor.getVnfd()) {

            List<VimInstance> vimInstances = new ArrayList<>();

            for (VirtualDeploymentUnit vdu : vnfd.getVdu())
                vimInstances.add(vimInstanceRepository.findFirstByName(vdu.getVimInstanceName()));

            //Creating the extension
            Map<String, String> extension = new HashMap<>();
            extension.put("nsr-id", networkServiceRecord.getId());

            // Setting extension in CoreMassage

            NFVMessage message;
            VNFPackage vnfPackage = vnfPackageRepository.findFirstById(vnfd.getVnfPackageLocation());
            message = new OrVnfmInstantiateMessage(vnfd, getDeploymentFlavour(vnfd), vnfd.getName(), networkServiceRecord.getVlr(), extension, vimInstances, vnfPackage);

            VnfmManagerEndpoint endpoint = vnfmRegister.getVnfm(vnfd.getEndpoint());
            if (endpoint == null) {
                throw new NotFoundException("VnfManager of type " + vnfd.getType() + " (endpoint = " + vnfd.getEndpoint() + ") is not registered");
            }

            VnfmSender vnfmSender;
            try {
                vnfmSender = this.getVnfmSender(endpoint.getEndpointType());
            } catch (BeansException e) {
                throw new NotFoundException(e);
            }

            vnfmSender.sendCommand(message, endpoint);
        }
        return new AsyncResult<>(null);
    }

    private void fillVnfrNames(NetworkServiceDescriptor networkServiceDescriptor, Set<VNFRecordDependency> dependencies, Map<String, Integer> vnfrNamesWeighted) {
        for (VirtualNetworkFunctionDescriptor virtualNetworkFunctionDescriptor : networkServiceDescriptor.getVnfd())
            vnfrNamesWeighted.put(virtualNetworkFunctionDescriptor.getName(), getWeightForVNFR(virtualNetworkFunctionDescriptor, networkServiceDescriptor));
    }

    private int getWeightForVNFR(VirtualNetworkFunctionDescriptor virtualNetworkFunctionDescriptor, NetworkServiceDescriptor networkServiceDescriptor) {
        int result = 0;
        for (VNFDependency dependency : networkServiceDescriptor.getVnf_dependency()) {
            if (dependency.getTarget().getName().equals(virtualNetworkFunctionDescriptor.getName())) {
                result++;
                result += getWeightForVNFR(dependency.getSource(), networkServiceDescriptor);
            }
        }

        return result;
    }

    //As a default operation of the NFVO, it get always the first DeploymentFlavour!
    private VNFDeploymentFlavour getDeploymentFlavour(VirtualNetworkFunctionDescriptor vnfd) throws NotFoundException {
        if (!vnfd.getDeployment_flavour().iterator().hasNext())
            throw new NotFoundException("There are no DeploymentFlavour in vnfd: " + vnfd.getName());
        return vnfd.getDeployment_flavour().iterator().next();
    }

    @Override
    public VnfmSender getVnfmSender(EndpointType endpointType) throws BeansException {
        String senderName = endpointType.toString().toLowerCase() + "VnfmSender";
        return (VnfmSender) this.context.getBean(senderName);
    }

    @Override
    public String executeAction(NFVMessage nfvMessage) throws VimException, NotFoundException, ExecutionException, InterruptedException {

        String actionName = nfvMessage.getAction().toString().replace("_", "").toLowerCase();
        String beanName = actionName + "Task";
        log.debug("Looking for bean called: " + beanName);
        AbstractTask task = (AbstractTask) context.getBean(beanName);

        task.setAction(nfvMessage.getAction());

        VirtualNetworkFunctionRecord virtualNetworkFunctionRecord;
        if (nfvMessage.getAction().ordinal() == Action.INSTANTIATE.ordinal()) {
            VnfmOrInstantiateMessage vnfmOrInstantiate = (VnfmOrInstantiateMessage) nfvMessage;
            virtualNetworkFunctionRecord = vnfmOrInstantiate.getVirtualNetworkFunctionRecord();
        } else if (nfvMessage.getAction().ordinal() == Action.SCALED.ordinal()) {
            VnfmOrScaledMessage vnfmOrScaled = (VnfmOrScaledMessage) nfvMessage;
            virtualNetworkFunctionRecord = vnfmOrScaled.getVirtualNetworkFunctionRecord();
            ((ScaledTask) task).setVnfcInstance(vnfmOrScaled.getVnfcInstance());
        } else if (nfvMessage.getAction().ordinal() == Action.HEAL.ordinal()) {
            OrVnfmHealVNFRequestMessage orVnfmHealVNFRequestMessage = (OrVnfmHealVNFRequestMessage) nfvMessage;
            virtualNetworkFunctionRecord = orVnfmHealVNFRequestMessage.getVirtualNetworkFunctionRecord();
        } else if (nfvMessage.getAction().ordinal() == Action.ALLOCATE_RESOURCES.ordinal()) {
            VnfmOrAllocateResourcesMessage vnfmOrAllocateResourcesMessage = (VnfmOrAllocateResourcesMessage) nfvMessage;
            virtualNetworkFunctionRecord = vnfmOrAllocateResourcesMessage.getVirtualNetworkFunctionRecord();
        } else {
            VnfmOrGenericMessage vnfmOrGeneric = (VnfmOrGenericMessage) nfvMessage;
            virtualNetworkFunctionRecord = vnfmOrGeneric.getVirtualNetworkFunctionRecord();
            task.setDependency(vnfmOrGeneric.getVnfRecordDependency());
        }

        virtualNetworkFunctionRecord.setTask(actionName);
        task.setVirtualNetworkFunctionRecord(virtualNetworkFunctionRecord);

        log.debug("Executing Task " + beanName + " for vnfr " + virtualNetworkFunctionRecord.getName() + ". Cyclic=" + virtualNetworkFunctionRecord.hasCyclicDependency());

        if (nfvMessage.getAction().ordinal() == Action.ALLOCATE_RESOURCES.ordinal() || nfvMessage.getAction().ordinal() == Action.GRANT_OPERATION.ordinal())
            return gson.toJson(asyncExecutor.submit(task).get());
        else {
            asyncExecutor.submit(task);
            return null;
        }
    }

    @Override
    public synchronized void findAndSetNSRStatus(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) {

        if (virtualNetworkFunctionRecord == null) {
            return;
        }

        log.debug("The nsr id is: " + virtualNetworkFunctionRecord.getParent_ns_id());

        Status status = Status.TERMINATED;
        NetworkServiceRecord networkServiceRecord;
        try {
            networkServiceRecord = nsrRepository.findFirstById(virtualNetworkFunctionRecord.getParent_ns_id());
        } catch (NoResultException e) {
            log.error("No NSR found with id " + virtualNetworkFunctionRecord.getParent_ns_id());
            return;
        }
        log.debug("Checking the status of NSR: " + networkServiceRecord.getName());

        for (VirtualNetworkFunctionRecord vnfr : networkServiceRecord.getVnfr()) {
            log.debug("VNFR " + vnfr.getName() + " is in state: " + vnfr.getStatus());
            if (status.ordinal() > vnfr.getStatus().ordinal()) {
                status = vnfr.getStatus();
            }
        }

        log.debug("Setting NSR status to: " + status);
        networkServiceRecord.setStatus(status);
        networkServiceRecord = nsrRepository.save(networkServiceRecord);
        log.debug("Now the status is: " + networkServiceRecord.getStatus());
        if (status.ordinal() == Status.ACTIVE.ordinal()) {
            //Check if all vnfr have been received from the vnfm
            boolean nsrFilledWithAllVnfr = nsdRepository.findFirstById(networkServiceRecord.getDescriptor_reference()).getVnfd().size() == networkServiceRecord.getVnfr().size();
            if (nsrFilledWithAllVnfr)
                publishEvent(Action.INSTANTIATE_FINISH, networkServiceRecord);
            else log.debug("Nsr is ACTIVE but not all vnfr have been received");
        } else if (status.ordinal() == Status.TERMINATED.ordinal()) {
            publishEvent(Action.RELEASE_RESOURCES_FINISH, networkServiceRecord);
            nsrRepository.delete(networkServiceRecord);
        }

        log.debug("Thread: " + Thread.currentThread().getId() + " finished findAndSet");
    }

    private void publishEvent(Action action, Serializable payload) {
        ApplicationEventNFVO event = new ApplicationEventNFVO(action, payload);
        EventNFVO eventNFVO = new EventNFVO(this);
        eventNFVO.setEventNFVO(event);
        log.debug("Publishing event: " + event);
        publisher.publishEvent(eventNFVO);
    }

    @Override
    @Async
    public Future<Void> sendMessageToVNFR(VirtualNetworkFunctionRecord virtualNetworkFunctionRecordDest, NFVMessage nfvMessage) throws NotFoundException {
        VnfmManagerEndpoint endpoint = vnfmRegister.getVnfm(virtualNetworkFunctionRecordDest.getEndpoint());
        if (endpoint == null) {
            throw new NotFoundException("VnfManager of type " + virtualNetworkFunctionRecordDest.getType() + " (endpoint = " + virtualNetworkFunctionRecordDest.getEndpoint() + ") is not registered");
        }
        VnfmSender vnfmSender;
        try {

            vnfmSender = this.getVnfmSender(endpoint.getEndpointType());
        } catch (BeansException e) {
            throw new NotFoundException(e);
        }

        log.debug("Sending message " + nfvMessage.getAction() + " to " + virtualNetworkFunctionRecordDest.getName());
        vnfmSender.sendCommand(nfvMessage, endpoint);
        return new AsyncResult<Void>(null);
    }

    @Override
    @Async
    public Future<Void> release(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) throws NotFoundException {
        VnfmManagerEndpoint endpoint = vnfmRegister.getVnfm(virtualNetworkFunctionRecord.getEndpoint());
        if (endpoint == null) {
            throw new NotFoundException("VnfManager of type " + virtualNetworkFunctionRecord.getType() + " (endpoint = " + virtualNetworkFunctionRecord.getEndpoint() + ") is not registered");
        }

        OrVnfmGenericMessage orVnfmGenericMessage = new OrVnfmGenericMessage(virtualNetworkFunctionRecord, Action.RELEASE_RESOURCES);
        VnfmSender vnfmSender;
        try {

            vnfmSender = this.getVnfmSender(endpoint.getEndpointType());
        } catch (BeansException e) {
            throw new NotFoundException(e);
        }

        vnfmSender.sendCommand(orVnfmGenericMessage, endpoint);
        return new AsyncResult<>(null);
    }

    @Override
    @Async
    public Future<Void> addVnfc(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, VNFComponent component, VNFRecordDependency dependency) throws NotFoundException {
        VnfmManagerEndpoint endpoint = vnfmRegister.getVnfm(virtualNetworkFunctionRecord.getEndpoint());
        if (endpoint == null) {
            throw new NotFoundException("VnfManager of type " + virtualNetworkFunctionRecord.getType() + " (endpoint = " + virtualNetworkFunctionRecord.getEndpoint() + ") is not registered");
        }

        OrVnfmScalingMessage message = new OrVnfmScalingMessage();
        message.setAction(Action.SCALE_OUT);
        message.setVirtualNetworkFunctionRecord(virtualNetworkFunctionRecord);
        message.setComponent(component);
        message.setDependency(dependency);
        VnfmSender vnfmSender;
        try {

            vnfmSender = this.getVnfmSender(endpoint.getEndpointType());
        } catch (BeansException e) {
            throw new NotFoundException(e);
        }

        vnfmSender.sendCommand(message, endpoint);
        return new AsyncResult<>(null);
    }

    @Override
    @Async
    public Future<Void> removeVnfcDependency(VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, VNFCInstance vnfcInstance) throws NotFoundException {
        VnfmManagerEndpoint endpoint = vnfmRegister.getVnfm(virtualNetworkFunctionRecord.getEndpoint());
        if (endpoint == null) {
            throw new NotFoundException("VnfManager of type " + virtualNetworkFunctionRecord.getType() + " (endpoint = " + virtualNetworkFunctionRecord.getEndpoint() + ") is not registered");
        }

        OrVnfmScalingMessage message = new OrVnfmScalingMessage();

        message.setAction(Action.SCALE_IN);
        message.setVirtualNetworkFunctionRecord(virtualNetworkFunctionRecord);
        message.setVnfcInstance(vnfcInstance);
        VnfmSender vnfmSender;
        try {

            vnfmSender = this.getVnfmSender(endpoint.getEndpointType());
        } catch (BeansException e) {
            throw new NotFoundException(e);
        }

        vnfmSender.sendCommand(message, endpoint);
        return new AsyncResult<>(null);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @Override
    public synchronized void onApplicationEvent(EventFinishNFVO event) {
        VirtualNetworkFunctionRecord virtualNetworkFunctionRecord = event.getEventNFVO().getVirtualNetworkFunctionRecord();
        publishEvent(event.getEventNFVO().getAction(), virtualNetworkFunctionRecord);
        if ((event.getEventNFVO().getAction().ordinal() != Action.ALLOCATE_RESOURCES.ordinal()) && (event.getEventNFVO().getAction().ordinal() != Action.GRANT_OPERATION.ordinal())) {
            findAndSetNSRStatus(virtualNetworkFunctionRecord);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        init();
    }

    @Override
    public Map<String, Map<String, Integer>> getVnfrNames() {
        return vnfrNames;
    }
}
