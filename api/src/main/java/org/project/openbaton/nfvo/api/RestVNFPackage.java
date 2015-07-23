/*
 * Copyright (c) 2015 Fraunhofer FOKUS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.project.openbaton.nfvo.api;

import org.project.openbaton.catalogue.nfvo.VNFPackage;
import org.project.openbaton.nfvo.core.interfaces.VNFPackageManagement;
import org.project.openbaton.nfvo.exceptions.NotFoundException;
import org.project.openbaton.nfvo.exceptions.VimException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vnf-packages")
public class RestVNFPackage {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VNFPackageManagement vnfPackageManagement;

	/**
	 * Adds a new VNFPackage to the VNFPackages repository
	 * 
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody VNFPackage onboard(@RequestParam("name") String name, @RequestParam("diskFormat") String diskFormat, @RequestParam("containerFormat") String containerFormat, @RequestParam("minDisk") int minDisk, @RequestParam("minRam") int minRam, @RequestParam("isPublic") boolean isPublic,  @RequestParam("file") MultipartFile file) throws IOException, VimException, NotFoundException, SQLException {
		if (!file.isEmpty()) {
			byte[] bytes = file.getBytes();
			return vnfPackageManagement.onboard(bytes, name, diskFormat, containerFormat, minDisk, minRam, isPublic);
		}
		else throw new IOException("File is empty!");
	}

	/**
	 * Removes the VNFPackage from the VNFPackages repository
	 * 
	 * @param id
	 *            : the id of configuration to be removed
	 */

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		vnfPackageManagement.delete(id);
	}

	/**
	 * Returns the list of the VNFPackages available
	 * 
	 * @return List<VNFPackage>: The list of VNFPackages available
	 */
	@RequestMapping(method = RequestMethod.GET)
	public List<VNFPackage> findAll() {
		return vnfPackageManagement.query();
	}

	/**
	 * Returns the VNFPackage selected by id
	 * 
	 * @param id
	 *            : The id of the VNFPackage
	 * @return VNFPackage: The VNFPackage selected
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public VNFPackage findById(@PathVariable("id") String id) {
		return vnfPackageManagement.query(id);
	}

	/**
	 * Updates the VNFPackage
	 * 
	 * @param vnfPackage_new
	 *            : The VNFPackage to be updated
	 * @param id
	 *            : The id of the VNFPackage
	 * @return VNFPackage The VNFPackage updated
	 */

	@RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public VNFPackage update( @RequestBody @Valid VNFPackage vnfPackage_new, @PathVariable("id") String id) {
		return vnfPackageManagement.update(id, vnfPackage_new);
	}
}
