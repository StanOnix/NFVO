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
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.catalogue.mano.descriptor.VNFDependency;
import org.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
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
    private NetworkServiceDescriptor networkServiceDescriptor;
    private String target;

    public NetworkServiceDescriptor getNetworkServiceDescriptor() {
        return networkServiceDescriptor;
    }

    public void setNSDandSourceName(NetworkServiceDescriptor networkServiceDescriptor, String target) {
        this.networkServiceDescriptor = networkServiceDescriptor;
        this.target = target;
    }

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


            pr.setVersion(returnString("version", propertiesMap));
            pr.setVendor(returnString("vendor", propertiesMap));

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

    private String returnString(String field, Map<String, Object> propertiesMap) {
        if (propertiesMap.get(field) == null)
            return "";
        else
            return propertiesMap.get(field).toString();

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

        Map<String, Object> life = (Map<String, Object>) interfaces;
        for (String lifeCycleTag : life.keySet()) {
//            System.out.println(lifeCycleTag);
            if (lifeCycleTag.toLowerCase().equals("standard")) {
//                System.out.println(lifeCycleTag.toLowerCase());
                return lifecyclesStandard(life, lifeCycleTag);
            } else if (lifeCycleTag.toLowerCase().equals("openbaton.interfaces.lifecycle") ||
                    lifeCycleTag.toLowerCase().equals("lifecycle")) {
//                System.out.println(lifeCycleTag.toLowerCase());

                return lifeCycleEventsOpenbaton(life, lifeCycleTag);


            }

        }

        Set<LifecycleEvent> lifeCyclesNull = new HashSet<>();
        return lifeCyclesNull;
    }

    private Set<LifecycleEvent> lifecyclesStandard(Map<String, Object> life, String lifeCycleTag) {

        Set<LifecycleEvent> lifecycleEvents = new HashSet<>();
//        System.out.println(((Map<String, Object>) life.get(lifeCycleTag)).keySet());

//        InterfaceStandard interfaceStandard = (InterfaceStandard) life.get(lifeCycleTag);
        for (String lifecycleName : ((Map<String, Object>) life.get(lifeCycleTag)).keySet()) {
            LifecycleEvent lifecycleEvent = new LifecycleEvent();
//            InterfaceStandard interfaceStandard  = new InterfaceStandard();
            if (lifecycleName.toLowerCase().equals("create")) {
//                System.out.println(((Map<String, Object>) life.get(lifeCycleTag)).get(lifecycleName));

                lifecycleEvent.setEvent(Event.INSTANTIATE);
//                interfaceStandard.setStart((String) ((Map<String, Object>) life.get(lifeCycleTag)).get(lifecycleName));
            } else if (lifecycleName.toLowerCase().equals("configure"))
                lifecycleEvent.setEvent(Event.CONFIGURE);
            else if (lifecycleName.toLowerCase().equals("start"))
                lifecycleEvent.setEvent(Event.START);
            else if (lifecycleName.toLowerCase().equals("delete"))
                lifecycleEvent.setEvent(Event.TERMINATE);
            else if (lifecycleName.toLowerCase().equals("stop"))
                lifecycleEvent.setEvent(Event.STOP);
            if (lifecycleEvent.getLifecycle_events() == null)
                lifecycleEvent.setLifecycle_events(new ArrayList<String>());
            if (lifecycleEvent.getEvent().equals(Event.CONFIGURE)) {
                Map<String, Object> configureEvent = (Map<String, Object>) ((Map<String, Object>) life.get(lifeCycleTag)).get(lifecycleName);
//                System.out.println(configureEvent);
                lifecycleEvent.getLifecycle_events().add((String) configureEvent.get("implementation"));
                Map<String, Object> inputsObj = (Map<String, Object>) configureEvent.get("inputs");
                for (String inputValue : inputsObj.keySet()) {
//                    System.out.println(inputsObj.get(inputValue));
//                    System.out.println(inputValue);
                    for (String configValues : inputsObj.keySet()) {
                        Map<String, Set<String>> valuesArray = (Map<String, Set<String>>) inputsObj.get(configValues);

//                        System.out.println(inputsObj.get(configValues));
//                        System.out.println(valuesArray);
                        for (String getValues : valuesArray.keySet()) {
                            System.out.println(valuesArray.get(getValues));
                            List<String> valueParamWithSource = (List<String>) valuesArray.get(getValues);
                            Set<String> valueParams = new LinkedHashSet<>(valueParamWithSource.subList(1, valueParamWithSource.size()));
                            VNFDependency dependency = new VNFDependency();
                            VirtualNetworkFunctionDescriptor source = new VirtualNetworkFunctionDescriptor();
                            VirtualNetworkFunctionDescriptor target = new VirtualNetworkFunctionDescriptor();


                            //In TOSCA Simple definitions is possible to retrieve PROPERTY from the same VNF
                            if (valueParamWithSource.get(0).equals("SELF"))
                                source.setName(this.target);
                            else
                                source.setName(valueParamWithSource.get(0));
                            target.setName(this.target);
                            dependency.setSource(source);
                            dependency.setTarget(target);
                            dependency.setParameters(valueParams);
                            if (this.networkServiceDescriptor.getVnf_dependency() == null)
                                this.networkServiceDescriptor.setVnf_dependency(new HashSet<VNFDependency>());
                            this.networkServiceDescriptor.getVnf_dependency().add(dependency);
                        }

                    }
                }

            } else
                lifecycleEvent.getLifecycle_events().add(((Map<String, String>) life.get(lifeCycleTag)).get(lifecycleName));
            lifecycleEvents.add(lifecycleEvent);

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

    private Set<LifecycleEvent> lifeCycleEventsOpenbaton(Map<String, Object> life, String lifeCycleTag) {
        Set<LifecycleEvent> lifecycleEvents = new HashSet<>();
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
        return lifecycleEvents;
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
