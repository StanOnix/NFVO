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
package org.openbaton.tosca.tests;

import com.google.gson.Gson;
import org.junit.Test;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.tosca.catalogue.Definitions;
import org.openbaton.tosca.parser.ParserTosca;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dbo on 12/11/15.
 */
public class ToscaTest {

    @Test
    public void test() throws FileNotFoundException {

        InputStream tosca = new FileInputStream(new File("src/main/resources/toscav1.yaml"));
        Constructor constructor = new Constructor(Definitions.class);
        TypeDescription projectDesc = new TypeDescription(Definitions.class);

//        projectDesc.putMapPropertyType("properties", String.class, PropertiesVnf.class);

        constructor.addTypeDescription(projectDesc);

        Yaml yaml = new Yaml(constructor);
        Definitions definitions = yaml.loadAs(tosca, Definitions.class);
        Gson gson = new Gson();
        ParserTosca parserTosca = new ParserTosca(definitions);

        NetworkServiceDescriptor nsd = parserTosca.getNetworkServiceDescriptor();

        System.out.println(gson.toJson(nsd));


    }
}
