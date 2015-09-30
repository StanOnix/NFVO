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


package org.project.openbaton.nfvo.core.test;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openbaton.catalogue.mano.common.*;
import org.openbaton.catalogue.mano.descriptor.*;
import org.openbaton.catalogue.mano.record.*;
import org.openbaton.catalogue.nfvo.*;
import org.openbaton.catalogue.nfvo.Configuration;
import org.openbaton.nfvo.main.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



/**
 * Created by tbr on 23.09.15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
@ComponentScan(basePackages = { "org.project.openbaton.nfvo" })
public class ApiDocumentation {

    Gson gson = new Gson();


    @Rule
    public final RestDocumentation restDocumentation = new RestDocumentation("/opt/openbaton/nfvo/build/generated-snippets");
    private RestDocumentationResultHandler document;
    private static Logger log = LoggerFactory.getLogger(ApiDocumentation.class);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private NetworkServiceDescriptor nsd, nsd1, nsd2;

    private Set< VirtualNetworkFunctionDescriptor> vnfd = new HashSet<VirtualNetworkFunctionDescriptor>();


    private VirtualNetworkFunctionDescriptor vnfd1, vnfd2, vnfd3, vnfd4, vnfd5, vnfd6, vnfd7;

    private VNFDependency vnfdep1;

    private PhysicalNetworkFunctionDescriptor pnfd1, pnfd2;

    private Set<VNFForwardingGraphDescriptor> vnffgd = new HashSet<VNFForwardingGraphDescriptor>();
    private VNFForwardingGraphDescriptor vnffgd1 = new VNFForwardingGraphDescriptor();

    private Set<VirtualLinkDescriptor> vld = new HashSet<VirtualLinkDescriptor>();

    private NetworkServiceRecord nsr = new NetworkServiceRecord();

    private VirtualNetworkFunctionRecord vnfr = new VirtualNetworkFunctionRecord();

    private VimInstance vimInstance = new VimInstance();
    private VimInstance vimInstance2 = new VimInstance();

    private Configuration config1 = new Configuration();




    @Before
    public void setUp() {


        this.document = document("{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));

        log.debug("ContextClas: " + context.getClass().getName());

        this.mockMvc = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.document)
                .build();



        nsd = new NetworkServiceDescriptor();
        Set<VNFDependency> vnf_dependency = new HashSet<VNFDependency>();
        Set<PhysicalNetworkFunctionDescriptor> pnfd = new HashSet<PhysicalNetworkFunctionDescriptor>();
        Security nsd_security = new Security();
        nsd_security.setVersion(1);
        vnffgd1.setVersion("1");
        vnffgd1.setVendor("Fokus");
        vnffgd1.setDescriptor_version("1");
        vnffgd.add(vnffgd1);
        nsd.setVnfd(vnfd);
        nsd.setVnf_dependency(vnf_dependency);
        nsd.setName("nsd0");
        nsd.setVersion("1");
        nsd.setVendor("Fokus");
        nsd.setVld(vld);

        nsd1 = new NetworkServiceDescriptor();
        nsd1.setName("nsd0");
        nsd1.setVersion("1");
        nsd1.setVendor("Fokus");
        nsd1.setVnffgd(vnffgd);
        nsd1.setVld(vld);
        nsd1.setService_deployment_flavour(new HashSet<DeploymentFlavour>());
        Set<String> monitoringParams = new HashSet<String>();
        monitoringParams.add("monitoringParam1");
        nsd1.setMonitoring_parameter(monitoringParams);
        Set<AutoScalePolicy> autoScalePolicies = new HashSet<AutoScalePolicy>();
        AutoScalePolicy asp = new AutoScalePolicy();
        autoScalePolicies.add(asp);
        nsd1.setAuto_scale_policy(autoScalePolicies);
        nsd1.setVnfd(vnfd);

        nsd2 = new NetworkServiceDescriptor();
        Security nsd_security2 = new Security();
        nsd_security2.setVersion(2);
        nsd2.setVnfd(vnfd);
        nsd2.setVnf_dependency(vnf_dependency);
        nsd2.setPnfd(pnfd);
        nsd2.setNsd_security(nsd_security2);
        nsd2.setName("nsd2");
        nsd2.setVersion("1");
        nsd2.setVendor("Fokus");
        nsd2.setVnffgd(vnffgd);
        nsd2.setService_deployment_flavour(new HashSet<DeploymentFlavour>());



        nsr.setVersion("2");
        nsr.setName("UpdatedNSR");
        nsr.setVendor("Fokus");
        nsr.setStatus(Status.ACTIVE);
        nsr.setVnfr(new HashSet<VirtualNetworkFunctionRecord>());
        nsr.setVlr(new HashSet<VirtualLinkRecord>());
        nsr.setVnf_dependency(new HashSet<VNFRecordDependency>());
        nsr.setLifecycle_event(new HashSet<LifecycleEvent>());
        nsr.setRuntime_policy_info("RuntimePolicyInfo");
        nsr.setDescriptor_reference("The descriptor reference");


        config1.setName("config1");


        vnfd1 = new VirtualNetworkFunctionDescriptor();
        vnfd2 = new VirtualNetworkFunctionDescriptor();
        vnfd3 = new VirtualNetworkFunctionDescriptor();
        vnfd4 = new VirtualNetworkFunctionDescriptor();
        vnfd5 = new VirtualNetworkFunctionDescriptor();
        vnfd6 = new VirtualNetworkFunctionDescriptor();
        vnfd7 = new VirtualNetworkFunctionDescriptor();
        vnfd1.setName("VNFD1");
        vnfd1.setType("Type1");
        vnfd2.setName("VNFD2");
        vnfd2.setType("Type2");
        vnfd3.setName("VNFD3");
        vnfd3.setType("Type3");
        vnfd3.setLifecycle_event(new HashSet<LifecycleEvent>());
        vnfd3.setConfigurations(new Configuration());
        vnfd3.setVdu(new HashSet<VirtualDeploymentUnit>());
        vnfd3.setVirtual_link(new HashSet<InternalVirtualLink>());
        vnfd3.setVdu_dependency(new HashSet<VDUDependency>());
        vnfd3.setManifest_file("manifestFile");
        vnfd3.setManifest_file_security(new HashSet<Security>());
        vnfd3.setEndpoint("An endpoint");
        vnfd3.setVnfPackage(new VNFPackage());
        vnfd3.setRequires(new HashSet<String>());
        vnfd3.setProvides(new HashSet<String>());
        vnfd3.setVendor("Fokus");
        vnfd3.setVld(new HashSet<VirtualLinkDescriptor>());
        vnfd3.setConnection_point(new HashSet<ConnectionPoint>());
        vnfd3.setMonitoring_parameter(new HashSet<String>());
        vnfd3.setConfigurations(config1);
        vnfd4.setName("VNFD4");
        vnfd4.setType("Type4");
        vnfd5.setName("VNFD5");
        vnfd5.setType("Type5");
        vnfd6.setName("VNFD6");
        vnfd6.setType("Type6");
        vnfd7.setName("VNFD7");
        vnfd7.setType("Type7");

        vnfdep1 = new VNFDependency();
        vnfdep1.setVersion(1);
        vnfdep1.setSource(vnfd7);

        pnfd1 = new PhysicalNetworkFunctionDescriptor();
        pnfd2 = new PhysicalNetworkFunctionDescriptor();
        pnfd1.setVersion("1");
        pnfd2.setVersion("2");

        vimInstance.setType("test");
        vimInstance.setImages(new HashSet<NFVImage>());
        vimInstance.setName("AVimInstance");
        vimInstance.setAuthUrl("aUrl");
        vimInstance.setLocation(new Location());
        vimInstance.setPassword("aSafePassword");
        vimInstance.setSecurityGroups(new HashSet<String>());
        vimInstance.setTenant("theTenant");
        vimInstance.setUsername("user");
        vimInstance.setVersion(1);
        vimInstance.setFlavours(new HashSet<DeploymentFlavour>());
        vimInstance2.setType("test");
        vimInstance2.setImages(new HashSet<NFVImage>());
        vimInstance2.setName("UpdatedVim");
        vimInstance2.setAuthUrl("aUrl");
        vimInstance2.setLocation(new Location());
        vimInstance2.setNetworks(new HashSet<Network>());
        vimInstance2.setPassword("12345");
        vimInstance2.setTenant("The tenant");
        vimInstance2.setUsername("user");


    }



    @Test
    public void nsdCreateExample() throws Exception {

        this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andDo(document("nsd-create-example", requestFields(
                        //fieldWithPath("auto_scale_policy").description("An array of the NSD's auto scale parameters"),
                        //fieldWithPath("connection_point").description("_"),
                        fieldWithPath("enabled").description("Is the NSD enabled or not"),
                        fieldWithPath("hb_version").type(JsonFieldType.STRING).description("_"),
                        //fieldWithPath("monitoring_parameter").description("An array of the NSD's monitoring parameters"),
                        fieldWithPath("name").description("The NSD's name"),
                        //fieldWithPath("nsd_security").description("A Security object"),
                        //fieldWithPath("pnfd").description("An array of PhysicalNetworkFunctionDescriptors"),
                        //fieldWithPath("service_deployment_flavour").
                        //        description("An array containing the NSD's NetworkServiceDeploymentFlavour"),
                        fieldWithPath("vendor").description("The vendor of the NSD"),
                        fieldWithPath("version").description("The NSD's version"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("An array of <<resources-VirtualNetworkFunctionDescriptor,VirtualNetworkFunctionDescriptors>>")
                        //fieldWithPath("vnffgd").description("An array of VNFForwardingGraphDescriptors")
                ))).
                andDo(document("nsd-create-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsdDeleteExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(delete("/api/v1/ns-descriptors/" + id)).
                andExpect(status().isNoContent());
    }


    @Test
    public void nsdGetAllExample() throws  Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");


        this.mockMvc.perform(get("/api/v1/ns-descriptors")).
                andExpect(status().isOk()).
                andDo(document("nsd-get-all-example", responseFields(
                        fieldWithPath("[]").description("The array of NetworkServiceDescriptors")
                ))).
                andDo(document("nsd-get-all-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void nsdGetExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");


        this.mockMvc.perform(get("/api/v1/ns-descriptors/" + id)).
                andExpect(status().isOk()).
                andDo(document("nsd-get-example", responseFields(
                        fieldWithPath("auto_scale_policy").description("_"),
                        fieldWithPath("connection_point").description("_"),
                        fieldWithPath("enabled").description("Is the NSD enabled or not"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("id").description("The NSD's id"),
                        fieldWithPath("monitoring_parameter").description("_"),
                        fieldWithPath("name").description("The NSD's name"),
                        fieldWithPath("nsd_security").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("pnfd").description("_"),
                        fieldWithPath("service_deployment_flavour").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("vendor").description("The vendor of the NSD"),
                        fieldWithPath("version").description("The NSD's version"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("An array of <<resources-VirtualNetworkFunctionDescriptor, VirtualNetworkFunctionDescriptors>>"),
                        fieldWithPath("vnffgd").description("_")
                ))).
                andDo(document("nsd-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }

    @Test
    public void nsdUpdateExample() throws Exception {


        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");


        this.mockMvc.perform(put("/api/v1/ns-descriptors/" + id).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isAccepted()).
                andDo(document("nsd-update-example", requestFields(
                        //fieldWithPath("auto_scale_policy").description("An array of the NSD's auto scale parameters"),
                        //fieldWithPath("connection_point").description("_"),
                        fieldWithPath("enabled").description("Is the NSD enabled or not"),
                        fieldWithPath("hb_version").description("_"),
                        //fieldWithPath("monitoring_parameter").description("An array of the NSD's monitoring parameters"),
                        fieldWithPath("name").description("The NSD's name"),
                        //fieldWithPath("nsd_security").description("A Security object"),
                        //fieldWithPath("pnfd").description("An array of PhysicalNetworkFunctionDescriptors"),
                        //fieldWithPath("service_deployment_flavour").
                        //        description("An array containing the NSD's NetworkServiceDeploymentFlavour"),
                        fieldWithPath("vendor").description("The vendor of the NSD"),
                        fieldWithPath("version").description("The NSD's version"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("An array of <<resources-VirtualNetworkFunctionDescriptor,VirtualNetworkFunctionDescriptors>>")
                        //fieldWithPath("vnffgd").description("An array of VNFForwardingGraphDescriptors")
                ))).
                andDo(document("nsd-update-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }





    @Test
    public void nsdGetVNFDExample() throws  Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        String createVNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfd1))).andReturn().getResponse().getContentAsString();

        String vnfdId = new JSONObject(createVNFDResponse).getString("id");

        this.mockMvc.perform(get("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/" + vnfdId)).
                andExpect(status().isAccepted()).
                andDo(document("nsd-get-v-n-f-d-example", responseFields(
                        fieldWithPath("auto_scale_policy").description("An array of the VNFD's auto scale parameters"),
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("connection_point").description("An array containing the VNFD's ConnectionPoints"),
                        fieldWithPath("deployment_flavour").description("_"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("_"), // TODO
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("id").description("The VNFD's id"),
                        fieldWithPath("lifecycle_event").description("An array of LifecyleEvents"),
                        fieldWithPath("manifest_file").type(JsonFieldType.STRING).description("A file that lists all files in the <<resources-VNFPackage, VNFPackage>>"),
                        fieldWithPath("manifest_file_security").description("Used for validating integrity and authenticity of the VNFD"),
                        fieldWithPath("monitoring_parameter").description("An array of the VNFD's monitoring parameters"),
                        fieldWithPath("name").description("The VNFD's name"),
                        fieldWithPath("provides").description("_"),// TODO
                        fieldWithPath("requires").description("_"),// TODO
                        fieldWithPath("service_deployment_flavour").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vendor").description("The vendor of the VNFD"),
                        fieldWithPath("version").description("The VNFD's version"),
                        fieldWithPath("virtual_link").description("Array of VirtualLinks, that represent the type of network connectivity mandated by the VNF vendor between two or more ConnectionPoints"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnffgd").description("_"),
                        fieldWithPath("vnfPackage").type(JsonFieldType.OBJECT).description("The <<resources-VNFPackage, VNFPackage>> in which this VNFD lies"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vdu_dependency").description("An array of elements that describe dependencies between <<components-VirtualDeploymentUnit, VDUs>>")
                ))).
                andDo(document("nsd-get-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }



    @Test
    public void nsdGetAllVNFDExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        String createVNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfd2))).andReturn().getResponse().getContentAsString();

        String vnfdId = new JSONObject(createVNFDResponse).getString("id");

        this.mockMvc.perform(get("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors")).
                andExpect(status().isAccepted()).
                andDo(document("nsd-get-all-v-n-f-d-example", responseFields(
                        fieldWithPath("[]").description("The array containing the VNFDs")
                ))).
                andDo(document("nsd-get-all-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }



    @Test
    public void nsdCreateVNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfd3))).
                andExpect(status().isCreated()).
                andDo(document("nsd-create-v-n-f-d-example", requestFields(
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to configure the Network Service"),
                        fieldWithPath("connection_point").description("Array of elements that describe external interfaces enabling connection with a VirtualLink"),
                        fieldWithPath("cyclicDependency").description("_"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("_"),// TODO
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("lifecycle_event").description("_"),// TODO
                        fieldWithPath("manifest_file").type(JsonFieldType.STRING).description("A file that lists all files in the <<resources-VNFPackage, VNFPackage>>"),
                        fieldWithPath("manifest_file_security").description("Used for validating integrity and authenticity of the VNFD"),
                        fieldWithPath("monitoring_parameter").description("_"),
                        fieldWithPath("name").description("The name of the VNFD"),
                        fieldWithPath("provides").description("_"),// TODO
                        fieldWithPath("requires").description("_"),// TODO
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vdu_dependency").description("An array of elements that describe dependencies between <<components-VirtualDeploymentUnit, VDUs>>"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the VNFD"),
                        fieldWithPath("virtual_link").description("Array of VirtualLinks, that represent the type of network connectivity mandated by the VNF vendor between two or more ConnectionPoints"),
                        fieldWithPath("vld").description("Array of VirtualLinkDescriptors, that provide descriptions of each Virtual Link"),
                        fieldWithPath("vnfPackage").type(JsonFieldType.OBJECT).description("The <<resources-VNFPackage, VNFPackage>> in which this VNFD lies")
                ))).
                andDo(document("nsd-create-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));


    }



    @Test
    public void nsdUpdateVNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        String createVNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfd4))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String vnfId = new JSONObject(createVNFDResponse).getString("id");

        this.mockMvc.perform(put("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/" + vnfId).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfd3))).
                andExpect(status().isAccepted()).
                andDo(document("nsd-update-v-n-f-d-example", requestFields(
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to configure the Network Service"),
                        fieldWithPath("connection_point").description("Array of elements that describe external interfaces enabling connection with a VirtualLink"),
                        fieldWithPath("cyclicDependency").description("_"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("_"),// TODO
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("lifecycle_event").description("_"),// TODO
                        fieldWithPath("manifest_file").type(JsonFieldType.STRING).description("A file that lists all files in the <<resources-VNFPackage, VNFPackage>>"),
                        fieldWithPath("manifest_file_security").description("Used for validating integrity and authenticity of the VNFD"),
                        fieldWithPath("monitoring_parameter").description("_"),
                        fieldWithPath("name").description("The name of the VNFD"),
                        fieldWithPath("provides").description("_"),// TODO
                        fieldWithPath("requires").description("_"),// TODO
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vdu_dependency").description("An array of elements that describe dependencies between <<components-VirtualDeploymentUnit, VDUs>>"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the VNFD"),
                        fieldWithPath("virtual_link").description("Array of VirtualLinks, that represent the type of network connectivity mandated by the VNF vendor between two or more ConnectionPoints"),
                        fieldWithPath("vld").description("Array of VirtualLinkDescriptors, that provide descriptions of each Virtual Link"),
                        fieldWithPath("vnfPackage").type(JsonFieldType.OBJECT).description("The <<resources-VNFPackage, VNFPackage>> in which this VNFD lies")
                ))).
                andDo(document("nsd-update-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void nsdDeleteVNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        String createVNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfd6))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String vnfId = new JSONObject(createVNFDResponse).getString("id");

        this.mockMvc.perform(delete("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/" + vnfId)).
                andExpect(status().isNoContent());
    }



    @Test
    public void nsrCreateExample() throws Exception {
        this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).
                andDo(document("nsr-create-example", requestFields(
                        fieldWithPath("auto_scale_policy").description("_"),
                        fieldWithPath("enabled").description("Is the NSR enabled or not"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("monitoring_parameter").description("_"),
                        fieldWithPath("name").description("The name of the NSR"),
                        fieldWithPath("service_deployment_flavour").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the NSR"),
                        fieldWithPath("version").description("The version of the NSR"),
                        fieldWithPath("vnfd").description("Array of the <<resources-VirtualNetworkFunctionDescriptor, VNFDs>> of the NSR"),
                        fieldWithPath("vnffgd").description("_"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors")

                ))).
                andDo(document("nsr-create-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void nsrCreateWithIdExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(post("/api/v1/ns-records/" + nsdId)).
                andExpect(status().isCreated());
    }



    @Test
    public void nsrGetAllExample() throws Exception {

        this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated());

        this.mockMvc.perform(get("/api/v1/ns-records/")).
                andExpect(status().isOk()).
                andDo(document("nsr-get-all-example", responseFields(
                        fieldWithPath("[]").description("The array of NSRs")
                ))).
                andDo(document("nsr-get-all-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }




    @Test
    public void nsrGetExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(get("/api/v1/ns-records/" + nsrId)).
                andExpect(status().isOk()).
                andDo(document("nsr-get-example", responseFields(
                        fieldWithPath("audit_log").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("auto_scale_policy").description("_"),
                        fieldWithPath("connection_point").description("_"),
                        fieldWithPath("descriptor_reference").type(JsonFieldType.STRING).description("The reference to the Network Service Descriptor used to instantiate this Network Service."),
                        fieldWithPath("id").description("The id of the NSR"),
                        fieldWithPath("lifecycle_event").description("Array of LifecycleEvents"),
                        fieldWithPath("lifecycle_event_history").description("Record of significant Network Service lifecycle events"),
                        fieldWithPath("monitoring_parameter").description("_"),
                        fieldWithPath("name").description("The name of the NSR"),
                        fieldWithPath("notification").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("pnfr").description("_"),
                        fieldWithPath("resource_reservation").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("runtime_policy_info").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("service_deployment_flavour").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("status").description("The status of the NSR"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the NSR"),
                        fieldWithPath("version").description("The version of the NSR"),
                        fieldWithPath("vlr").description("Array of VirtualLinkRecords that record resources used to implement a virtual link"),
                        fieldWithPath("vnf_dependency").description("Array of <<components-VNFRecordDependency, VNFRecordDependencies>>"),
                        fieldWithPath("vnffgr").description("_"),
                        fieldWithPath("vnfr").description("An array of <<resources-VirtualNetworkFunctionRecord, VirtualNetworkFunctionRecords>>")
                        ))).
                andDo(document("nsr-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsrUpdateExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(put("/api/v1/ns-records/" + nsrId).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsr))).
                andExpect(status().isAccepted()).
                andDo(document("nsr-update-example", requestFields(
                        fieldWithPath("descriptor_reference").type(JsonFieldType.STRING).description("The reference to the Network Service Descriptor used to instantiate this Network Service."),
                        fieldWithPath("lifecycle_event").description("An array of LifecycleEvents"),
                        fieldWithPath("name").description("The name of the NSR"),
                        fieldWithPath("runtime_policy_info").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("status").description("The status of the NSR"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the NSR"),
                        fieldWithPath("version").description("The version of the NSR"),
                        fieldWithPath("vlr").description("A record of the resources used to implement a virtual network link"),
                        fieldWithPath("vnf_dependency").description("An array of <<components-VNFRecordDependency, VNFRecordDependencies>>"),
                        fieldWithPath("vnfr").description("An array of <<resources-VirtualNetworkFunctionRecord, VirtualNetworkFunctionRecords>>")

                ))).
                andDo(document("nsr-update-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }





    @Test
    public void nsrDeleteExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

        String updateResponse = this.mockMvc.perform(put("/api/v1/ns-records/" + nsrId).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsr))).
                andExpect(status().isAccepted()).andReturn().getResponse().getContentAsString();

        nsrId = new JSONObject(updateResponse).getString("id");

        this.mockMvc.perform(delete("/api/v1/ns-records/" + nsrId))
                .andExpect(status().isNoContent());
    }



    @Test
    public void nsrGetAllVnfrExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(post("/api/v1/ns-records/" + nsrId + "/vnfrecords/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfr))).
                andExpect(status().isCreated());

        this.mockMvc.perform(get("/api/v1/ns-records/" + nsrId + "/vnfrecords/")).
                andExpect(status().isAccepted()).
                andDo(document("nsr-get-all-vnfr-example", responseFields(
                        fieldWithPath("[]").description("The array of NSRs")
                ))).
                andDo(document("nsr-get-all-vnfr-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsrGetVnfrExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(post("/api/v1/ns-records/" + nsrId + "/vnfrecords/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfr))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String vnfrResponse = this.mockMvc.perform(get("/api/v1/ns-records/" + nsrId + "/vnfrecords/")).andReturn().getResponse().getContentAsString();

        vnfrResponse = vnfrResponse.substring(1, vnfrResponse.length() - 1);
        String vnfrId = new JSONObject(vnfrResponse).getString("id");

        this.mockMvc.perform(get("/api/v1/ns-records/" + nsrId + "/vnfrecords/" + vnfrId)).
                andExpect(status().isAccepted()).
                andDo(document("nsr-get-vnfr-example", responseFields(
                        fieldWithPath("audit_log").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("auto_scale_policy").description("_"),
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to configure the Network Service"),
                        fieldWithPath("connected_external_virtual_link").description("Array of referencs to <<components-VirtualLinkRecord, VLRs>>"),
                        fieldWithPath("connection_point").description("_"),
                        fieldWithPath("descriptor_reference").type(JsonFieldType.STRING).description("The reference to the <<resources-VirtualNetworkFunctionDescriptor, VNFD>> used to instantiate this VNF"),
                        fieldWithPath("deployment_flavour_key").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("_"),// TODO
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("id").description("The id of the VNFR"),
                        fieldWithPath("lifecycle_event").description("Array of <<components-LifecycleEvent, LifecycleEvents>>"),
                        fieldWithPath("lifecycle_event_history").description("Record of significant Network Service lifecycle events"),
                        fieldWithPath("localization").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("monitoring_parameter").description("_"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("The name of the VNFR"),
                        fieldWithPath("notification").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("parent_ns_id").type(JsonFieldType.STRING).description("Reference to the <<resources-NetworkServiceRecord, NSRs>> that this VNFR is part of"),
                        fieldWithPath("provides").type(JsonFieldType.OBJECT).description("_"),// TODO
                        fieldWithPath("requires").type(JsonFieldType.OBJECT).description("_"),// TODO
                        fieldWithPath("runtime_policy_info").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("status").type(JsonFieldType.OBJECT).description("The status of the VNFR"),
                        fieldWithPath("task").type(JsonFieldType.STRING).description("_"),// TODO
                        fieldWithPath("type").type(JsonFieldType.STRING).description("The type of the VNFR"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the VNFR"),
                        fieldWithPath("version").type(JsonFieldType.STRING).description("The version of the VNFR"),
                        fieldWithPath("virtual_link").description("Internal Virtual Link instances used in this VNF"),
                        fieldWithPath("vnf_address").description("A network address (e.g. VLAN, IP) configured for the management access or other internal and external connection interface on this VNF"),
                        fieldWithPath("vnfm_id").type(JsonFieldType.STRING).description("The identification of the <<components-VirtualNetworkFunctionManager, VNFM>> entity managing this VNF"),
                        fieldWithPath("vnfPackage").type(JsonFieldType.OBJECT).description("The <<resources-VNFPackage, VNFPackage>> in which this VNFR lies")
                ))).
                andDo(document("nsr-get-vnfr-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsrDeleteVnfrExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd1))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(post("/api/v1/ns-records/" + nsrId + "/vnfrecords/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfr))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String vnfrResponse = this.mockMvc.perform(get("/api/v1/ns-records/" + nsrId + "/vnfrecords/")).andReturn().getResponse().getContentAsString();

        vnfrResponse = vnfrResponse.substring(1, vnfrResponse.length() - 1);
        String vnfrId = new JSONObject(vnfrResponse).getString("id");

        this.mockMvc.perform(delete("/api/v1/ns-records/" + nsrId + "/vnfrecords/" + vnfrId)).
                andExpect(status().isNoContent());
    }





    @Test
    public void vimInstanceCreateExample() throws Exception {
        this.mockMvc.perform(post("/api/v1/datacenters").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vimInstance))).
                andExpect(status().isCreated()).
                andDo(document("vim-instance-create-example", requestFields(
                        fieldWithPath("authUrl").description("_"), // TODO
                        fieldWithPath("flavours").description("_"),
                        fieldWithPath("images").description("An array of <<components-NFVImages, NFVImages>>"),
                        fieldWithPath("location").description("_"),// TODO
                        fieldWithPath("name").description("The name of the VimInstance"),
                        fieldWithPath("password").description("_"), // TODO
                        fieldWithPath("securityGroups").description("_"),
                        fieldWithPath("tenant").description("_"), // TODO
                        fieldWithPath("type").description("The type of the VimInstance"),
                        fieldWithPath("username").description("_"), // TODO
                        fieldWithPath("version").description("The version of the VimInstance")
                ))).
                andDo(document("vim-instance-create-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void vimInstanceGetAllExample() throws Exception {

        this.mockMvc.perform(post("/api/v1/datacenters").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vimInstance))).
                andExpect(status().isCreated());

        this.mockMvc.perform(get("/api/v1/datacenters")).
                andExpect(status().isOk()).
                andDo(document("vim-instance-get-all-example", responseFields(
                        fieldWithPath("[]").description("The array of VimInstances")
                ))).
                andDo(document("vim-instance-get-all-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void vimInstanceGetExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/datacenters").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vimInstance))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(get("/api/v1/datacenters/" + id)).
                andExpect(status().isOk()).
                andDo(document("vim-instance-get-example", responseFields(
                        fieldWithPath("authUrl").description("_"),// TODO
                        fieldWithPath("flavours").description("_"),
                        fieldWithPath("id").description("The id of the VimInstance"),
                        fieldWithPath("images").description("Array of <<components-NFVImage, NFVImages>>"),
                        fieldWithPath("keyPair").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("location").description("_"), // TODO
                        fieldWithPath("name").description("The name of the VimInstance"),
                        fieldWithPath("networks").description("_"), // TODO
                        fieldWithPath("password").description("_"), // TODO
                        fieldWithPath("securityGroups").description("_"),
                        fieldWithPath("tenant").description("_"),// TODO
                        fieldWithPath("type").description("The type of the VimInstance"),
                        fieldWithPath("username").description("_"),// TODO
                        fieldWithPath("version").description("The version of the VimInstance")
                ))).
                andDo(document("vim-instance-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void vimInstanceUpdateExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/datacenters").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vimInstance))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(put("/api/v1/datacenters/" + id).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vimInstance2))).
                andExpect(status().isAccepted()).
                andDo(document("vim-instance-update-example", requestFields(
                        fieldWithPath("authUrl").description("_"),// TODO
                        fieldWithPath("images").description("Array of <<components-NFVImage, NFVImages>>"),
                        fieldWithPath("location").description("_"),// TODO
                        fieldWithPath("name").description("The name of the VimInstance"),
                        fieldWithPath("networks").description("_"),// TODO
                        fieldWithPath("password").description("_"),// TODO
                        fieldWithPath("tenant").description("_"),// TODO
                        fieldWithPath("type").description("The type of the VimInstance"),
                        fieldWithPath("username").description("_"),// TODO
                        fieldWithPath("version").description("The version of the VimInstance")
                ))).
                andDo(document("vim-instance-update-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }

    @Test
    public void vimInstanceDeleteExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/datacenters").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vimInstance))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(delete("/api/v1/datacenters/" + id)).
                andExpect(status().isNoContent());
    }




    @Test
    public void vnfpackageGetAllExample() throws Exception {
        this.mockMvc.perform(get("/api/v1/vnf-packages")).
                andExpect(status().isOk());

    }


}