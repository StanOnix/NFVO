/*
 * Copyright (c) 2016 Fraunhofer FOKUS
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

/**
 * Created by dbo on 13/01/16.
 */
public class NodeTemplates {
    private Map<String,NodeTemplate> node_templates;

    public Map<String, NodeTemplate> getNode_templates() {
        return node_templates;
    }

    public void setNode_templates(Map<String, NodeTemplate> node_templates) {
        this.node_templates = node_templates;
    }
}
