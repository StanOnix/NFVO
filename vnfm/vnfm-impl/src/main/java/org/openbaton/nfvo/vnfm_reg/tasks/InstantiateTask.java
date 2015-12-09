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

import org.openbaton.catalogue.nfvo.messages.Interfaces.NFVMessage;
import org.openbaton.nfvo.vnfm_reg.tasks.abstracts.AbstractTask;
import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.catalogue.nfvo.Action;
import org.openbaton.catalogue.nfvo.messages.OrVnfmGenericMessage;
import org.openbaton.nfvo.core.interfaces.DependencyManagement;
import org.openbaton.nfvo.core.interfaces.DependencyQueuer;
import org.openbaton.vnfm.interfaces.sender.VnfmSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by lto on 06/08/15.
 */
@Service
@Scope("prototype")
public class InstantiateTask extends AbstractTask {

    @Autowired
    private DependencyManagement dependencyManagement;

    @Autowired
    private DependencyQueuer dependencyQueuer;

    @Override
    protected NFVMessage doWork() throws Exception {

        VnfmSender vnfmSender;
        vnfmSender = this.getVnfmSender(vnfmRegister.getVnfm(virtualNetworkFunctionRecord.getEndpoint()).getEndpointType());

        log.info("Instantiation is finished for vnfr: " + virtualNetworkFunctionRecord.getName() + " his nsr id father is:" + virtualNetworkFunctionRecord.getParent_ns_id());
        VirtualNetworkFunctionRecord existing = vnfrRepository.findFirstById(virtualNetworkFunctionRecord.getId());
        log.debug("VNFR arrived version= " + virtualNetworkFunctionRecord.getHb_version());
        log.debug("VNFR existing version= " + existing.getHb_version());
        saveVirtualNetworkFunctionRecord();

        dependencyManagement.fillParameters(virtualNetworkFunctionRecord);

        NetworkServiceRecord nsr = networkServiceRecordRepository.findFirstById(virtualNetworkFunctionRecord.getParent_ns_id());
        for (VirtualNetworkFunctionRecord vnfr : nsr.getVnfr())
            log.debug("Current Vnfrs in the database: " + vnfr.getName());
        dependencyQueuer.releaseVNFR(virtualNetworkFunctionRecord.getName(), nsr);
        log.debug("Calling dependency management for VNFR: " + virtualNetworkFunctionRecord.getName());
        int dep;
        dep = dependencyManagement.provisionDependencies(virtualNetworkFunctionRecord);
        log.debug("Found " + dep + " dependencies");
        if (dep == 0) {
            log.info("VNFR: " + virtualNetworkFunctionRecord.getName() + " (" + virtualNetworkFunctionRecord.getId() + ") has 0 dependencies, Calling START");
            log.debug("HIBERNATE VERSION IS: " + virtualNetworkFunctionRecord.getHb_version());
            vnfmSender.sendCommand(new OrVnfmGenericMessage(virtualNetworkFunctionRecord, Action.START), vnfmRegister.getVnfm(virtualNetworkFunctionRecord.getEndpoint()));
        }
        return null;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
