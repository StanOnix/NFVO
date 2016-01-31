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
import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.Test;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.tosca.catalogue.Definitions;
import org.openbaton.tosca.catalogue.exceptions.NotSupportedType;
import org.openbaton.tosca.parser.ParserCSAR;
import org.openbaton.tosca.parser.ParserTosca;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by dbo on 12/11/15.
 */
public class ToscaTest {

    @Test
    public void test() throws FileNotFoundException, NotSupportedType {

        InputStream tosca = new FileInputStream(new File("src/main/resources/toscav1.yaml"));
        Constructor constructor = new Constructor(Definitions.class);
        TypeDescription projectDesc = new TypeDescription(Definitions.class);

        constructor.addTypeDescription(projectDesc);

        Yaml yaml = new Yaml(constructor);
        Definitions definitions = yaml.loadAs(tosca, Definitions.class);
        Gson gson = new Gson();
        ParserTosca parserTosca = new ParserTosca();

        NetworkServiceDescriptor nsd = parserTosca.getNetworkServiceDescriptor(definitions);

        System.out.println(gson.toJson(nsd));


    }

//    @Test
    public void test2() throws IOException, ArchiveException {
        List<String> fileList;

        String inputFile = "/opt/openbaton/toscaNFVO/tosca-parser/src/main/resources/file.csar";
//        unZipIt(inputFile,OUTPUT_FOLDER);

        try {
            unzip2(inputFile);
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test3() throws Exception {

        String inputFile = "/opt/openbaton/toscaNFVO/tosca-parser/src/main/resources/file.csar";
        ParserCSAR parserCSAR = new ParserCSAR(inputFile);

        System.out.println(parserCSAR);

    }

    public void unzip2(String zipFile) throws IOException {

        System.out.println(zipFile);

        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = zipFile.substring(0, zipFile.length() - 5);
        System.out.println(newPath);

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        List<String> fileList = new ArrayList<>();
        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

            String currentEntry = entry.getName();
            fileList.add(currentEntry.trim());
//
//            System.out.println(currentEntry);
//            System.out.println(entry.isDirectory());


            File destFile = new File(newPath + '/' + currentEntry, currentEntry);
            destFile = new File(newPath, destFile.getName());

            File destinationParent = destFile.getParentFile();
            String pathEntry = "" + destinationParent + '/' + currentEntry;


            if (entry.isDirectory()) {
                new File(pathEntry).mkdir();
//                destFile = new File(pathEntry +"/"+ currentEntry, destFile.getName());

            }
            // create the parent directory structure if needed
//            destinationParent.mkdirs();
            if (!entry.isDirectory()) {
                destFile = new File(pathEntry);

                BufferedInputStream is = new BufferedInputStream(zip
                        .getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile.getAbsoluteFile());
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }
//            if (currentEntry.endsWith(".zip")) {
//                // found a zip file, try to open
//                unzip(destFile.getAbsolutePath());
//            }
        }

        if (fileList.contains("TOSCA-Metadata/TOSCA.meta"))
            System.out.println("################");
        System.out.println(fileList);

        FileInputStream fstream = new FileInputStream(newPath + "/TOSCA-Metadata/TOSCA.meta");
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        //Read File Line By Line
        while ((strLine = br.readLine()) != null) {
            // Print the content on the console
            System.out.println(strLine);
            String fileDefinition;
            String entryDefinition = "Entry-Definitions:";
            if (strLine.contains(entryDefinition)) {
                fileDefinition = strLine.substring(entryDefinition.length(), strLine.length()).trim();
                System.out.println(fileDefinition);
//ParserTosca parserTosca = new ParserTosca();

                if (!fileList.contains(fileDefinition))
                    //TODO Throw Exception if file is not inside the csar
                    System.out.println("####### ERROR ");


//                Definitions d = fromFileToDefinitions(newPath + '/'+ fileDefinition);

            }
        }
        in.close();


    }

    public Definitions fromFileToDefinitions(String fileName) throws FileNotFoundException {
        InputStream tosca = new FileInputStream(new File(fileName));
        Constructor constructor = new Constructor(Definitions.class);
        TypeDescription projectDesc = new TypeDescription(Definitions.class);

//        projectDesc.putMapPropertyType("properties", String.class, PropertiesVnf.class);

        constructor.addTypeDescription(projectDesc);

        Yaml yaml = new Yaml(constructor);
        Definitions definitions = yaml.loadAs(tosca, Definitions.class);
        return definitions;
    }
}
