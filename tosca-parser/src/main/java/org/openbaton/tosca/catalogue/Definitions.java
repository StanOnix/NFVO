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

import java.util.Map;
import java.util.Set;

/**
 * Created by dbo on 12/11/15.
 *
 * This class is based on TOSCA Simple Profile for Network Functions Virtualization (NFV) Version 1.0
 */
public class Definitions {
    private String tosca_definitions_version;
    private String tosca_default_namespace;
    private String description;
    private Metadata metadata;
    private Set<String> imports;
    private Map<String, NodeTemplate> topology_template;
    private Map<String, Object> dsl_definitions;
    private Map<String, Relationships> relationships_templete;

    public Map<String, Relationships> getRelationships_templete() {
        return relationships_templete;
    }

    public void setRelationships_templete(Map<String, Relationships> relationships_templete) {
        this.relationships_templete = relationships_templete;
    }

    public Set<String> getImports() {
        return imports;
    }

    public void setImports(Set<String> imports) {
        this.imports = imports;
    }

    public String getTosca_definitions_version() {
        return tosca_definitions_version;
    }

    public void setTosca_definitions_version(String tosca_definitions_version) {
        this.tosca_definitions_version = tosca_definitions_version;
    }

    public String getTosca_default_namespace() {
        return tosca_default_namespace;
    }

    public void setTosca_default_namespace(String tosca_default_namespace) {
        this.tosca_default_namespace = tosca_default_namespace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Map<String, NodeTemplate> getTopology_template() {
        return topology_template;
    }

    public void setTopology_template(Map<String, NodeTemplate> topology_template) {
        this.topology_template = topology_template;
    }

    public Map<String, Object> getDsl_definitions() {
        return dsl_definitions;
    }

    public void setDsl_definitions(Map<String, Object> dsl_definitions) {
        this.dsl_definitions = dsl_definitions;
    }

    @Override
    public String toString() {
        return "Definitions{" +
                "tosca_definitions_version='" + tosca_definitions_version + '\'' +
                ", tosca_default_namespace='" + tosca_default_namespace + '\'' +
                ", description='" + description + '\'' +
                ", metadata=" + metadata +
                ", imports=" + imports +
                ", topology_template=" + topology_template +
                ", dsl_definitions=" + dsl_definitions +
                ", relationships_templete=" + relationships_templete +
                '}';
    }
}
