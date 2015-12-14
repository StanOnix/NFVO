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

package org.openbaton.tosca.catalogue;

import org.openbaton.catalogue.mano.common.Event;
import org.openbaton.catalogue.mano.common.LifecycleEvent;
import org.openbaton.tosca.catalogue.interfaces.INodeTemplete;

import java.util.*;

/**
 * Created by dbo on 12/11/15.
 */
public class NodeTemplate implements INodeTemplete {
    private String type;
    private Object properties;
    private Object requirements;
    private Object relationships;
    private Object interfaces;
    private Object capabilities;
    private String virtualbinding;
    private String virtualLink;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getProperties() {
        PropertiesVnf pr = null;
        PropertiesVdu prVdu = null;
        String type = this.getType();
        Map<String, Object> propertiesMap = (Map<String, Object>) this.properties;
        if (type.startsWith("openbaton.type.VNF")) {

            pr = new PropertiesVnf();
            Configuration conf = new Configuration();


            pr.setVersion(tryExist("version", propertiesMap));
            pr.setVendor(tryExist("vendor", propertiesMap));

            Map<String, Object> configurationsMap = (Map<String, Object>) propertiesMap.get("configurations");
            conf.setName(configurationsMap.get("name").toString());
//                System.out.println(configurationsMap.get("configurationParameters"));
            conf.setConfigurationParameters((ArrayList<HashMap<String, String>>) configurationsMap.get("configurationParameters"));
            pr.setConfigurations(conf);

            ArrayList<HashMap<String, String>> deployment_flavour = (ArrayList<HashMap<String, String>>) propertiesMap.get("deployment_flavour");
            pr.setDeployment_flavour(deployment_flavour);
//                pr.setVirtual_link((ArrayList<HashMap<String, String>>) propertiesMap.get("virtual_link"));
//                System.out.print(pr);

            return pr;


        } else if (type.startsWith("openbaton.type.VDU")) {
            prVdu = new PropertiesVdu();
            prVdu.setScale_in_out((Integer) propertiesMap.get("scale_in_out"));
            prVdu.setVm_image((ArrayList<String>) propertiesMap.get("vm_image"));
            prVdu.setVimInstance((String) propertiesMap.get("vimInstanceName"));
//                System.out.println(prVdu);
            return prVdu;

        } else if (type.startsWith("tosca.nodes.nfv.CP")) {
            PropertiesCP cp = new PropertiesCP();
            if (propertiesMap != null)
                cp.setFloatingIp((String) propertiesMap.get("floatingIp"));
            return cp;
        }


        return properties;
    }

    private String tryExist(String field, Map<String, Object> propertiesMap) {
        try {
            return propertiesMap.get(field).toString();
        } catch (NullPointerException e) {
            System.out.println(e.toString());
        }
        return "";
    }

    public void setProperties(Object properties) {
        this.properties = properties;
    }

    public Object getRequirements() {
        return requirements;
    }

    public void setRequirements(Object requirements) {
        this.requirements = requirements;
    }

    public Object getRelationships() {
        return relationships;
    }

    public void setRelationships(Object relationships) {
        this.relationships = relationships;
    }

    public Object getInterfaces() {
        Set<LifecycleEvent> lifecycleEvents = new HashSet<>();
        Map<String, Object> life = (Map<String, Object>) interfaces;
        for (String lifeCycleTag : life.keySet()) {
            for (String lifecycleName : ((Map<String, Object>) life.get(lifeCycleTag)).keySet()) {
                LifecycleEvent lifecycleEvent = new LifecycleEvent();
                if (lifecycleName.equals("INSTANCIATE"))
                    lifecycleEvent.setEvent(Event.INSTANTIATE);
                else if (lifecycleName.equals("CONFIGURE"))
                    lifecycleEvent.setEvent(Event.CONFIGURE);
                else if (lifecycleName.equals("START"))
                    lifecycleEvent.setEvent(Event.START);
                else if (lifecycleName.equals("TERMINATE"))
                    lifecycleEvent.setEvent(Event.TERMINATE);

                lifecycleEvent.setLifecycle_events((List<String>) ((Map<String, Object>) life.get(lifeCycleTag)).get(lifecycleName));
                lifecycleEvents.add(lifecycleEvent);
            }

        }
        return lifecycleEvents;
    }

    public void setInterfaces(Object interfaces) {
        this.interfaces = interfaces;
    }

    public Object getCapabilities() {
        Map<String, Object> capabilitiesMap = (Map<String, Object>) this.capabilities;
        Map<String, Object> hostMap = (Map<String, Object>) capabilitiesMap.get("host");

//        System.out.println(hostMap.get("properties"));

        return capabilities;
    }

    public void setCapabilities(Object capabilities) {
        this.capabilities = capabilities;
    }

    public String getVirtualbinding() {
        return virtualbinding;
    }

    public void setVirtualbinding(String virtualbinding) {
        this.virtualbinding = virtualbinding;
    }

    public String getVirtualLink() {
        return virtualLink;
    }

    public void setVirtualLink(String virtualLink) {
        this.virtualLink = virtualLink;
    }

    @Override
    public String toString() {
        return "NodeTemplate{" +
                "type='" + type + '\'' +
                ", properties=" + properties +
                ", requirements=" + requirements +
                ", relationships=" + relationships +
                ", interfaces=" + interfaces +
                ", capabilities=" + capabilities +
                ", virtualbinding='" + virtualbinding + '\'' +
                ", virtualLink='" + virtualLink + '\'' +
                '}';
    }

    @Override
    public String type() {
        return type;
    }
}
