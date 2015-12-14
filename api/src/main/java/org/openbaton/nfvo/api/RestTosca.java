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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    private Yaml postTosca(@RequestBody String tosca){
        log.debug(tosca.toString());

        Yaml yaml= new Yaml();
        Map<String, Object> object = (Map<String, Object>) yaml.load(tosca.toString());
        Iterator it = object.entrySet().iterator();
        NetworkServiceDescriptor nsd = new NetworkServiceDescriptor();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<String> value = new ArrayList<String>();

        ArrayList<Object> obj = new ArrayList<Object>();
        ArrayList<Object> objected = new ArrayList<Object>();
        for (Object name : object.keySet()) {
            key.add(name.toString());
            obj.add(object.get(name));
            System.out.println(name.toString());
            System.out.println(object.get(name));
            if(name.toString().equals("topology_template")){
                Map<String, Object> topology_template = (Map<String, Object>) object.get(name);
                for (Object topoName : topology_template.keySet()) {
                    System.out.println("---"+topoName.toString());
                    System.out.println("---"+topology_template.get(topoName));
                    /*if(topoName.toString().equals("iperf-server")) {


                        Map<String,VNFTosca> vnf = (Map<String,VNFTosca>) topology_template.get(topoName);
                        System.out.println("Map<String, VNFTosca> ---------" +  vnf.get(0));

                        for (Object vnfObj : vnf.keySet()) {
                            System.out.println("VNFTosca ---------" +  vnfObj);
                            System.out.println("VNFTosca ---------" +  vnf.get(vnfObj));

                        }
                    }*/
                }

            }

        }
/*
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getKey().equals("tosca_definitions_version"))
                nsd.setName(pair.getValue().toString());

            if(pair.getKey() instanceof String)
                System.out.println("\n\n" + pair.getKey() + " = STRING " + pair.getValue());

            if(pair.getKey() instanceof Object)
                System.out.println("\n\n"+pair.getKey() + " OBJ " + pair.getValue());

            it.remove(); // avoids a ConcurrentModificationException

        }
*/
//        log.debug(object.toString());
        return yaml;
    }

}
