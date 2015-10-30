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

package org.openbaton.nfvo.core.core;

import org.openbaton.catalogue.mano.descriptor.VNFComponent;
import org.openbaton.catalogue.mano.descriptor.VNFDConnectionPoint;
import org.openbaton.catalogue.mano.descriptor.VirtualDeploymentUnit;
import org.openbaton.catalogue.mano.record.VNFCInstance;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.catalogue.nfvo.Server;
import org.openbaton.catalogue.nfvo.VimInstance;
import org.openbaton.exceptions.VimException;
import org.openbaton.nfvo.vim_interfaces.vim.VimBroker;
import org.openbaton.vim.drivers.exceptions.VimDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by lto on 11/06/15.
 */
@Service
@Scope("prototype")
@ConfigurationProperties(prefix = "activemq")
public class ResourceManagement implements org.openbaton.nfvo.core.interfaces.ResourceManagement {

    private String brokerIp;

    @Value("${nfvo.monitoring.ip}")
    private String monitoringIp;

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VimBroker vimBroker;

    public String getBrokerIp() {
        return brokerIp;
    }

    public void setBrokerIp(String brokerIp) {
        this.brokerIp = brokerIp;
    }

    @Override
    public List<String> allocate(VirtualDeploymentUnit virtualDeploymentUnit, VirtualNetworkFunctionRecord virtualNetworkFunctionRecord) throws VimException, VimDriverException, ExecutionException, InterruptedException {
        org.openbaton.nfvo.vim_interfaces.resource_management.ResourceManagement vim;
        vim = vimBroker.getVim(virtualDeploymentUnit.getVimInstance().getType());
        log.debug("Executing allocate with Vim: " + vim.getClass().getSimpleName());
        List<String> ids = new ArrayList<>();
        log.debug("NAME: " + virtualNetworkFunctionRecord.getName());
        log.debug("ID: " + virtualDeploymentUnit.getId());
        String hostname = virtualNetworkFunctionRecord.getName().replaceAll("_", "-");
        log.debug("Hostname is: " + hostname);
        virtualDeploymentUnit.setHostname(hostname);
        for (VNFComponent component : virtualDeploymentUnit.getVnfc()) {
            log.trace("UserData is: " + getUserData(virtualNetworkFunctionRecord.getEndpoint()));
            Map<String, String> floatinIps = new HashMap<>();
            for (VNFDConnectionPoint connectionPoint : component.getConnection_point()){
                if (connectionPoint.getFloatingIp() != null)
                    floatinIps.put(connectionPoint.getVirtual_link_reference(),connectionPoint.getFloatingIp());
            }
            log.info("FloatingIp chosen are: " + floatinIps);
            VNFCInstance added = vim.allocate(virtualDeploymentUnit, virtualNetworkFunctionRecord, component, getUserData(virtualNetworkFunctionRecord.getEndpoint()), floatinIps).get();
            ids.add(added.getVc_id());
            if (floatinIps.size() > 0 && (added.getFloatingIps() == null || added.getFloatingIps().size() == 0))
                log.warn("NFVO wasn't able to associate FloatingIPs. Is there enough available?");
        }
        return ids;
    }

    private String allocateVNFC(VirtualDeploymentUnit virtualDeploymentUnit, VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, org.openbaton.nfvo.vim_interfaces.resource_management.ResourceManagement vim, VNFComponent component) throws InterruptedException, ExecutionException, VimException, VimDriverException {
        log.trace("UserData is: " + getUserData(virtualNetworkFunctionRecord.getEndpoint()));
        Map<String, String> floatinIps = new HashMap<>();
        for (VNFDConnectionPoint connectionPoint : component.getConnection_point()){
            floatinIps.put(connectionPoint.getVirtual_link_reference(),connectionPoint.getFloatingIp());
        }
        log.info("FloatingIp chosen are: " + floatinIps);
        VNFCInstance added = vim.allocate(virtualDeploymentUnit, virtualNetworkFunctionRecord, component, getUserData(virtualNetworkFunctionRecord.getEndpoint()), floatinIps).get();
        added.setVnfComponent(component);
        if (floatinIps.size() > 0 && added.getFloatingIps().size() == 0)
            log.warn("NFVO wasn't able to associate FloatingIPs. Is there enough available");
        return added.getVim_id();
    }

    private String getUserData(String endpoint) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/etc/openbaton/openbaton.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.debug("Loaded: " + properties);
        String url = properties.getProperty("spring.activemq.broker-url");
        String activeIp = (String) url.subSequence(6, url.indexOf(":61616"));
        log.debug("Active ip is: " + brokerIp);
        log.debug("Monitoring ip is: " + monitoringIp);
        String result = "#!/bin/bash\n" +
                "echo \"deb http://get.openbaton.org/repos/apt/debian/ ems main\" >> /etc/apt/sources.list\n" +
                "apt-get install git -y\n" +
                "wget -O - http://get.openbaton.org/public.gpg.key | apt-key add -\n" +
                "apt-get update\n";

        if (monitoringIp != null){
            result += " echo \"Installing zabbix-agent for server at _address\"\n" +
                    "sudo apt-get install -y zabbix-agent\n" +
                    "sudo sed -i -e 's/ServerActive=127.0.0.1/ServerActive=" + monitoringIp + ":10051/g' -e 's/Server=127.0.0.1/Server=" + monitoringIp + "/g' -e 's/Hostname=/#Hostname=/g' /etc/zabbix/zabbix_agentd.conf\n" +
                    "sudo service zabbix-agent restart\n" +
                    "sudo rm zabbix-release_2.2-1+precise_all.deb\n" +
                    "echo \"finished installing zabbix-agent!\"\n";
        }

        result +=
                "apt-get install -y python-pip\n" +
                "apt-get install -y ems\n" +
                "mkdir -p /etc/openbaton/ems\n" +
                "echo [ems] > /etc/openbaton/ems/conf.ini\n" +
                "echo orch_ip=" + brokerIp + " >> /etc/openbaton/ems/conf.ini\n" +
                "export hn=`hostname`\n" +
                "echo \"type=" + endpoint + "\" >> /etc/openbaton/ems/conf.ini\n" +
                "echo \"hostname=$hn\" >> /etc/openbaton/ems/conf.ini\n" +
                "echo orch_port=61613 >> /etc/openbaton/ems/conf.ini\n" +
                "/opt/openbaton/ems/ems.sh start\n";

        return result;
    }

    @Override
    public List<Server> query(VimInstance vimInstance) throws VimException {
        return vimBroker.getVim(vimInstance.getType()).queryResources(vimInstance);
    }

    @Override
    public void update(VirtualDeploymentUnit vdu) {

    }

    @Override
    public void scale(VirtualDeploymentUnit vdu) {

    }

    @Override
    public void migrate(VirtualDeploymentUnit vdu) {

    }

    @Override
    public void operate(VirtualDeploymentUnit vdu, String operation) {

    }

    @Override
    public Future<Void> release(VirtualDeploymentUnit virtualDeploymentUnit, VNFCInstance vnfcInstance) throws VimException, ExecutionException, InterruptedException {
        org.openbaton.nfvo.vim_interfaces.resource_management.ResourceManagement vim = vimBroker.getVim(virtualDeploymentUnit.getVimInstance().getType());
        log.debug("Removing vnfcInstance: " + vnfcInstance);
        vim.release(vnfcInstance, virtualDeploymentUnit.getVimInstance()).get();
        return new AsyncResult<>(null);
    }

    @Override
    public void createReservation(VirtualDeploymentUnit vdu) {

    }

    @Override
    public void queryReservation() {

    }

    @Override
    public void updateReservation(VirtualDeploymentUnit vdu) {

    }

    @Override
    public void releaseReservation(VirtualDeploymentUnit vdu) {

    }

    @Override
    public String allocate(VirtualDeploymentUnit virtualDeploymentUnit, VirtualNetworkFunctionRecord virtualNetworkFunctionRecord, VNFComponent componentToAdd) throws InterruptedException, ExecutionException, VimException, VimDriverException {
        org.openbaton.nfvo.vim_interfaces.resource_management.ResourceManagement vim;
        vim = vimBroker.getVim(virtualDeploymentUnit.getVimInstance().getType());
        log.debug("Executing allocate with Vim: " + vim.getClass().getSimpleName());
        log.debug("NAME: " + virtualNetworkFunctionRecord.getName());
        log.debug("ID: " + virtualDeploymentUnit.getId());
        return allocateVNFC(virtualDeploymentUnit, virtualNetworkFunctionRecord, vim, componentToAdd);
    }
}
