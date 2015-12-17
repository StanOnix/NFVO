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

import org.openbaton.catalogue.mano.descriptor.VirtualDeploymentUnit;
import org.openbaton.catalogue.mano.record.VNFCInstance;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.catalogue.nfvo.messages.Interfaces.NFVMessage;
import org.openbaton.nfvo.repositories.VNFCInstanceRepository;
import org.openbaton.nfvo.vnfm_reg.tasks.abstracts.AbstractTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by lto on 06/08/15.
 */
@Service
@Scope("prototype")
public class StartTask extends AbstractTask {

    @Autowired
    private VNFCInstanceRepository vnfcInstanceRepository;

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public NFVMessage doWork() throws Exception {
        log.debug("----> STARTED VNFR: " + virtualNetworkFunctionRecord.getName());
        VirtualNetworkFunctionRecord existing = vnfrRepository.findFirstById(virtualNetworkFunctionRecord.getId());
        log.debug("vnfr arrived version= " + virtualNetworkFunctionRecord.getHb_version());
        log.debug("vnfr existing version= " + existing.getHb_version());

//        VirtualNetworkFunctionRecord existingvnfr = vnfrRepository.findOne(virtualNetworkFunctionRecord.getId());
//        log.debug("vnfr existing version= " + existingvnfr.getHb_version());

        for (VirtualDeploymentUnit virtualDeploymentUnit : virtualNetworkFunctionRecord.getVdu()){
            for (VNFCInstance vnfcInstance : virtualDeploymentUnit.getVnfc_instance()){
//                vnfcInstanceRepository.save(vnfcInstance);
                log.debug("VNFCI arrived version: " + vnfcInstance.getVersion());
            }
        }

        for (VirtualDeploymentUnit virtualDeploymentUnit : existing.getVdu()){
            for (VNFCInstance vnfcInstance : virtualDeploymentUnit.getVnfc_instance()){
                log.debug("VNFCI existing version: " + vnfcInstance.getVersion());
            }
        }

        saveVirtualNetworkFunctionRecord();
        return null;
    }
}
