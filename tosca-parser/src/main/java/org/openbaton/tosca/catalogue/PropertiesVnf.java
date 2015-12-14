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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dbo on 16/11/15.
 *
 * Properties for Virtual Network Function
 */
public class PropertiesVnf extends Properties {

    private Configuration configurations;
    private ArrayList<HashMap<String, String>> deployment_flavour;

    public Configuration getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Configuration configurations) {
        this.configurations = configurations;
    }

    public ArrayList<HashMap<String, String>> getDeployment_flavour() {
        return deployment_flavour;
    }

    public void setDeployment_flavour(ArrayList<HashMap<String, String>> deployment_flavour) {
        this.deployment_flavour = deployment_flavour;
    }

    @Override
    public String toString() {
        return "PropertiesVnf{" +
                "configurations=" + configurations +
                ", deployment_flavour=" + deployment_flavour +
                '}';
    }
}
