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
 * Created by dbo on 14/01/16.
 */
public class InterfaceStandard {
    private String create;
    private Configure configure;
    private String start;
    private String stop;
    private String delete;

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public Configure getConfigure() {
        return configure;
    }

    public void setConfigure(Configure configure) {
        this.configure = configure;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    @Override
    public String toString() {
        return "InterfaceStandard{" +
                "create='" + create + '\'' +
                ", start='" + start + '\'' +
                ", stop='" + stop + '\'' +
                ", configure=" + configure +
                '}';
    }

    class Configure{
        String implementations;
        Map<String,Object> input;

        public String getImplementations() {
            return implementations;
        }

        public void setImplementations(String implementations) {
            this.implementations = implementations;
        }

        public Map<String, Object> getInput() {
            return input;
        }

        public void setInput(Map<String, Object> input) {
            this.input = input;
        }

        @Override
        public String toString() {
            return "Configure{" +
                    "implementations='" + implementations + '\'' +
                    ", input=" + input +
                    '}';
        }
    }


}
