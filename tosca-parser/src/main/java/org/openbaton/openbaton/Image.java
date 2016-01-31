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

package org.openbaton.openbaton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbo on 27/01/16.
 */
public class Image {
    private String upload;
    private List<String> names;
    private List<String> ids;
    private String link;

    public Image() {
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public List<String> getNames() {

        if(this.names==null)
            this.names=new ArrayList<>();

        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }


    @Override
    public String toString() {
        return "Image{" +
                "upload='" + upload + '\'' +
                ", names=" + names +
                ", ids=" + ids +
                ", link='" + link + '\'' +
                '}';
    }
}
