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

package org.openbaton.nfvo.api;


import org.openbaton.tosca.parser.ParserCSAR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * Created by dbo on 05/01/16.
 */
@RestController
@RequestMapping("/api/v1/csar")
public class RestCSAR {

    @Autowired
    private ParserCSAR parserCSAR;
    @Autowired
    private HttpServletRequest request;

    private Logger log = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String onboard(@RequestParam("file") MultipartFile csar) throws Exception {


        if (!csar.isEmpty()) {
            String filePath = request.getServletContext().getRealPath("/tmp/");
            File csarPath = new File(filePath);
            csar.transferTo(csarPath);
            log.debug(String.valueOf(csarPath.exists()));
            parserCSAR.storeScriptsFromCSAR(csarPath.getAbsolutePath());

        } else throw new IOException("File is empty!");
        return "Correctly stored the CSAR file";
    }
}
