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

/**
 * Created by dbo on 17/11/15.
 *
 * Properties for the Virtual Deployment Unit
 */
public class PropertiesVdu extends Properties{

    private ArrayList<String> vm_image;
    private int scale_in_out;
    private String vimInstance;

    public ArrayList<String> getVm_image() {
        return vm_image;
    }

    public void setVm_image(ArrayList<String> vm_image) {
        this.vm_image = vm_image;
    }

    public int getScale_in_out() {
        return scale_in_out;
    }

    public void setScale_in_out(int scale_in_out) {
        this.scale_in_out = scale_in_out;
    }

    public String getVimInstance() {
        return vimInstance;
    }

    public void setVimInstance(String vimInstance) {
        this.vimInstance = vimInstance;
    }

    @Override
    public String toString() {
        return "PropertiesVdu{" +
                "vm_image=" + vm_image +
                ", scale_in_out=" + scale_in_out +
                ", vimInstance='" + vimInstance + '\'' +
                '}';
    }
}
