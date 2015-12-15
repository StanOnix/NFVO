/*
 * Copyright (c) 2015 Fraunhofer FOKUS
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.openbaton.nfvo.api;

import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.exceptions.BadFormatException;
import org.openbaton.exceptions.NetworkServiceIntegrityException;
import org.openbaton.exceptions.NotFoundException;
import org.openbaton.nfvo.core.interfaces.NetworkServiceDescriptorManagement;
import org.openbaton.tosca.catalogue.Definitions;
import org.openbaton.tosca.parser.ParserTosca;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Created by dbo on 29/10/15.
 */
@RestController
@RequestMapping("/api/v1/tosca")

public class RestTosca {
    private Logger log = LoggerFactory.getLogger(this.getClass());
/*
@Autowired
private ToscaManagment toscaManagment;*/

    @Autowired
    private NetworkServiceDescriptorManagement networkServiceDescriptorManagement;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    private Yaml postTosca(@RequestBody String tosca) throws NetworkServiceIntegrityException, BadFormatException, NotFoundException {
        log.debug(tosca.toString());


        Constructor constructor = new Constructor(Definitions.class);
        TypeDescription projectDesc = new TypeDescription(Definitions.class);

        constructor.addTypeDescription(projectDesc);

        Yaml yaml = new Yaml(constructor);
        Definitions definitions = yaml.loadAs(tosca, Definitions.class);
        log.debug(definitions.toString());

        ParserTosca parserTosca = new ParserTosca(definitions);
        NetworkServiceDescriptor nsd = parserTosca.getNetworkServiceDescriptor();

        networkServiceDescriptorManagement.onboard(nsd);

        return yaml;
    }

}
