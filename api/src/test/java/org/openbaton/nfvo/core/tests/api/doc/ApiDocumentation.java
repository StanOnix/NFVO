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


package org.openbaton.nfvo.core.tests.api.doc;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openbaton.catalogue.mano.common.*;
import org.openbaton.catalogue.mano.descriptor.*;
import org.openbaton.catalogue.mano.record.*;
import org.openbaton.catalogue.nfvo.*;
import org.openbaton.exceptions.BadFormatException;
import org.openbaton.exceptions.QuotaExceededException;
import org.openbaton.nfvo.api.*;
import org.openbaton.nfvo.core.interfaces.*;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



/**
 * Created by tbr on 23.09.15.
 */

public class ApiDocumentation {


    private RestDocumentationResultHandler document;

    @Rule
    public final RestDocumentation restDocumentation = new RestDocumentation("./build/api-documentation/generated-snippets");

    private MockMvc mockMvc;


    @Mock
    private NetworkServiceDescriptorManagement networkServiceDescriptorManagement;

    @InjectMocks
    private RestNetworkServiceDescriptor restNetworkServiceDescriptor;


    @Mock
    private VirtualNetworkFunctionManagement vnfdManagement;

    @InjectMocks
    private RestVirtualNetworkFunctionDescriptor restVirtualNetworkFunctionDescriptor;


    @Mock
    private NetworkServiceRecordManagement networkServiceRecordManagement;

    @InjectMocks
    private RestNetworkServiceRecord restNetworkServiceRecord;



    @Mock
    private VimManagement vimManagement;

    @InjectMocks
    private RestVimInstances restVimInstances;



    @Mock
    private VNFPackageManagement vnfPackageManagement;

    @InjectMocks
    private RestVNFPackage restVNFPackage;




    private Gson gson = new Gson();


    private Set<NetworkServiceDescriptor> nsdSet = new HashSet<NetworkServiceDescriptor>();
    private NetworkServiceDescriptor iperfNsd1 = new NetworkServiceDescriptor();
    private Set<VirtualNetworkFunctionDescriptor> iperfVnfdSet = new HashSet<VirtualNetworkFunctionDescriptor>();
    private Set<VirtualLinkDescriptor> iperfVldSet = new HashSet<VirtualLinkDescriptor>();
    private Set<VNFDependency> iperfVnfdepSet = new HashSet<VNFDependency>();
    private VNFDependency iperfDep = new VNFDependency();
    private VirtualNetworkFunctionDescriptor iperfVnfdServer = new VirtualNetworkFunctionDescriptor();
    private VirtualNetworkFunctionDescriptor iperfVnfdClient = new VirtualNetworkFunctionDescriptor();
    private Configuration configuration = new Configuration();
    private Set<ConfigurationParameter> confParameters = new HashSet<ConfigurationParameter>();
    private ConfigurationParameter confPar = new ConfigurationParameter();
    private Set<VirtualDeploymentUnit> vduServerSet = new HashSet<VirtualDeploymentUnit>();
    private VirtualDeploymentUnit vduServer = new VirtualDeploymentUnit();
    private Set<String> imageSet = new HashSet<String>();
    private Set<VNFComponent> vnfcSet = new HashSet<VNFComponent>();
    private VNFComponent vnfc = new VNFComponent();
    private Set<VNFDConnectionPoint> vnfdConnectionPointSet = new HashSet<VNFDConnectionPoint>();
    private VNFDConnectionPoint vnfdConnectionPoint = new VNFDConnectionPoint();
    private Set<InternalVirtualLink> vlSet = new HashSet<InternalVirtualLink>();
    private InternalVirtualLink vl = new InternalVirtualLink();
    private Set<LifecycleEvent> leServerSet = new HashSet<LifecycleEvent>();
    private Set<LifecycleEvent> leClientSet = new HashSet<LifecycleEvent>();
    private LifecycleEvent inst = new LifecycleEvent();
    private LifecycleEvent startServer = new LifecycleEvent();
    private LifecycleEvent startClient = new LifecycleEvent();
    private List<String> instScripts = new LinkedList<String>();
    private List<String> startServerScripts = new LinkedList<String>();
    private List<String> startClientScripts = new LinkedList<String>();
    private Set<VNFDeploymentFlavour> deploymentFlavSet = new HashSet<VNFDeploymentFlavour>();
    private VNFDeploymentFlavour deplFlav = new VNFDeploymentFlavour();
    private Set<VirtualDeploymentUnit> vduClientSet = new HashSet<VirtualDeploymentUnit>();
    private VirtualDeploymentUnit vduClient = new VirtualDeploymentUnit();
    private Set<String> depParamSet = new HashSet<String>();
    private Set<VirtualNetworkFunctionDescriptor> vnfdSet = new HashSet<VirtualNetworkFunctionDescriptor>();
    private VNFPackage vnfPackage0 = new VNFPackage();
    private VNFPackage vnfPackage1 = new VNFPackage();
    private VNFPackage vnfPackage1Return = new VNFPackage();
    private Set<VNFPackage> vnfPackageSet = new HashSet<VNFPackage>();
    private NFVImage image = new NFVImage();
    private Set<Script> scripts = new HashSet<Script>();
    private Script install = new Script();
    private Script start = new Script();
    private Location berlin = new Location();
    private VimInstance vimInstance0 = new VimInstance();
    private VimInstance vimInstance0Return = new VimInstance();
    private VimInstance vimInstance1 = new VimInstance();
    private VimInstance vimInstance1Return = new VimInstance();
    private Set<VimInstance> vimInstanceSet = new HashSet<VimInstance>();
    private NetworkServiceRecord nsr0 = new NetworkServiceRecord();
    private NetworkServiceRecord nsr1 = new NetworkServiceRecord();
    private NetworkServiceRecord nsr1Return = new NetworkServiceRecord();
    private NetworkServiceRecord nsr2 = new NetworkServiceRecord();
    private Set<NetworkServiceRecord> nsrSet = new HashSet<NetworkServiceRecord>();
    private VirtualNetworkFunctionRecord vnfr0 = new VirtualNetworkFunctionRecord();
    private VirtualNetworkFunctionRecord vnfr1 = new VirtualNetworkFunctionRecord();
    private Set<VirtualNetworkFunctionRecord> vnfrSet = new HashSet<VirtualNetworkFunctionRecord>();


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        this.document = document("{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));



        this.mockMvc = MockMvcBuilders.standaloneSetup(restNetworkServiceDescriptor, restVirtualNetworkFunctionDescriptor,
                restNetworkServiceRecord, restVimInstances, restVNFPackage)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.document)
                .build();



        nsdSet.add(iperfNsd1);

        iperfNsd1.setName("iperf-nsd");
        iperfNsd1.setVendor("FOKUS");
        iperfNsd1.setVersion("1");
        iperfNsd1.setVnfd(iperfVnfdSet);
        iperfNsd1.setVld(iperfVldSet);
        iperfNsd1.setVnf_dependency(iperfVnfdepSet);
        iperfNsd1.setVld(iperfVldSet);
        iperfNsd1.setVnf_dependency(iperfVnfdepSet);

        iperfVnfdSet.add(iperfVnfdServer);
        iperfVnfdSet.add(iperfVnfdClient);

        iperfVnfdServer.setName("iperf-server");
        iperfVnfdServer.setVersion("1");
        iperfVnfdServer.setVendor("FOKUS");
        iperfVnfdServer.setType("server");
        iperfVnfdServer.setEndpoint("generic");
        iperfVnfdServer.setConfigurations(configuration);
        iperfVnfdServer.setVdu(vduServerSet);
        iperfVnfdServer.setVirtual_link(vlSet);
        iperfVnfdServer.setLifecycle_event(leServerSet);
        iperfVnfdServer.setDeployment_flavour(deploymentFlavSet);

        configuration.setName("config_name");
        configuration.setConfigurationParameters(confParameters);

        confParameters.add(confPar);

        confPar.setConfKey("answer");
        confPar.setValue("42");

        vduServerSet.add(vduServer);

        vduServer.setVm_image(imageSet);
        vduServer.setVimInstanceName("vim-instance");
        vduServer.setScale_in_out(1);
        vduServer.setVnfc(vnfcSet);

        imageSet.add("name of an image");

        vnfcSet.add(vnfc);

        vnfc.setConnection_point(vnfdConnectionPointSet);

        vnfdConnectionPointSet.add(vnfdConnectionPoint);

        vnfdConnectionPoint.setVirtual_link_reference("networkA");

        vlSet.add(vl);

        vl.setName("networkA");

        leServerSet.add(inst);
        leServerSet.add(startServer);
        leClientSet.add(inst);
        leClientSet.add(startClient);

        inst.setEvent(Event.INSTANTIATE);
        inst.setLifecycle_events(instScripts);

        startServer.setEvent(Event.START);
        startServer.setLifecycle_events(startServerScripts);

        startClient.setEvent(Event.START);
        startClient.setLifecycle_events(startClientScripts);

        instScripts.add("install.sh");
        startServerScripts.add("start-server.sh");
        startClientScripts.add("server_start-client.sh");

        deploymentFlavSet.add(deplFlav);

        deplFlav.setFlavour_key("m1.small");
        deplFlav.setRam(2048);
        deplFlav.setDisk(5);
        deplFlav.setVcpus(2);



        iperfVnfdClient.setName("iperf-client");
        iperfVnfdClient.setVersion("1");
        iperfVnfdClient.setVendor("FOKUS");
        iperfVnfdClient.setType("client");
        iperfVnfdClient.setEndpoint("generic");
        iperfVnfdClient.setConfigurations(configuration);
        iperfVnfdClient.setVdu(vduClientSet);
        iperfVnfdClient.setVirtual_link(vlSet);
        iperfVnfdClient.setLifecycle_event(leClientSet);
        iperfVnfdClient.setDeployment_flavour(deploymentFlavSet);

        vduClientSet.add(vduClient);

        vduClient.setVm_image(imageSet);
        vduClient.setVimInstanceName("vim-instance");
        vduClient.setScale_in_out(2);
        vduClient.setVnfc(vnfcSet);

        iperfDep.setSource(iperfVnfdServer);
        iperfDep.setTarget(iperfVnfdClient);
        iperfDep.setParameters(depParamSet);

        depParamSet.add("networkA");

        vnfdSet.add(iperfVnfdClient);
        vnfdSet.add(iperfVnfdServer);

        vnfPackage0.setName("An example of a VNFPackage");
        vnfPackage0.setId("61a40e7c-a424-4d47-8413-532217ddcf4f");
        vnfPackage0.setImageLink("URL to the image file");
        vnfPackage0.setScriptsLink("URL to the place where the scripts can be downloaded");
        vnfPackage0.setImage(image);

        vnfPackage1.setName("An updated VNFPackage");
        vnfPackage1.setVersion(2);
        vnfPackage1.setImageLink("URL to the image file");
        vnfPackage1.setScriptsLink("URL to the place where the scripts can be downloaded");
        vnfPackage1.setImage(image);

        vnfPackage1Return.setName("An updated VNFPackage");
        vnfPackage1Return.setVersion(2);
        vnfPackage1Return.setId("61a40e7c-a424-4d47-8413-532217ddcf4f");
        vnfPackage1Return.setImageLink("URL to the image file");
        vnfPackage1Return.setScriptsLink("URL to the place where the scripts can be downloaded");
        vnfPackage1Return.setImage(image);
        vnfPackage1Return.setScripts(scripts);

        vnfPackageSet.add(vnfPackage0);

        image.setName("ubuntu_server_image");

        scripts.add(install);
        scripts.add(start);

        install.setName("install.sh");
        start.setName("start.sh");

        berlin.setName("Berlin");
        berlin.setLatitude("52.525876");
        berlin.setLongitude("13.314400");

        vimInstance0.setName("An example of a VimInstance");
        vimInstance0.setAuthUrl("A Url");
        vimInstance0.setPassword("12345");
        vimInstance0.setTenant("demo");
        vimInstance0.setType("Type1");
        vimInstance0.setUsername("User");
        vimInstance0.setLocation(berlin);

        vimInstance0Return.setName("An example of a VimInstance");
        vimInstance0Return.setAuthUrl("A Url");
        vimInstance0Return.setPassword("12345");
        vimInstance0Return.setTenant("demo");
        vimInstance0Return.setType("Type1");
        vimInstance0Return.setUsername("User");
        vimInstance0Return.setLocation(berlin);
        // additional fields
        vimInstance0Return.setId("07241c3b-9d50-44ba-a495-9d3b96c226bd");

        vimInstance1.setName("An updated VimInstance");
        vimInstance1.setAuthUrl("A Url");
        vimInstance1.setPassword("123456789");
        vimInstance1.setTenant("demo");
        vimInstance1.setType("Type2");
        vimInstance1.setUsername("User");
        vimInstance1.setLocation(berlin);
        //additional fields
        vimInstance1Return.setId("07241c3b-9d50-44ba-a495-9d3b96c226bd");

        vimInstanceSet.add(vimInstance0Return);

        nsr0.setName("An example NSR");
        nsr0.setVersion("1.0");
        nsr0.setVendor("FOKUS");
        nsr0.setId("66046d77-aade-4f14-ad39-f2976532e5f2");
        nsr0.setDescriptor_reference("c60ec37a-654a-4605-93fb-5a1932db6ecf");

        nsr1.setName("An example NSR");
        nsr1.setVersion("1.0");
        nsr1.setDescriptor_reference("c60ec37a-654a-4605-93fb-5a1932db6ecf");
        nsr1.setVendor("FOKUS");
        nsr1.setStatus(Status.ACTIVE);
        nsr1.setVlr(new HashSet<VirtualLinkRecord>());
        nsr1.setVnf_dependency(new HashSet<VNFRecordDependency>());
        nsr1.setVnfr(new HashSet<VirtualNetworkFunctionRecord>());

        nsr1Return.setName("An example NSR");
        nsr1Return.setVersion("1.0");
        nsr1Return.setDescriptor_reference("c60ec37a-654a-4605-93fb-5a1932db6ecf");
        nsr1Return.setVendor("FOKUS");
        nsr1Return.setStatus(Status.ACTIVE);
        nsr1Return.setVlr(new HashSet<VirtualLinkRecord>());
        nsr1Return.setVnf_dependency(new HashSet<VNFRecordDependency>());
        nsr1Return.setVnfr(new HashSet<VirtualNetworkFunctionRecord>());
        // additional fields
        nsr1Return.setId("66046d77-aade-4f14-ad39-f2976532e5f2");


        nsr2.setName("An example NSR");
        nsr2.setVersion("1.0");
        nsr2.setDescriptor_reference("c60ec37a-654a-4605-93fb-5a1932db6ecf");
        nsr2.setVendor("FOKUS");
        nsr2.setStatus(Status.ACTIVE);
        nsr2.setVlr(new HashSet<VirtualLinkRecord>());
        nsr2.setVnf_dependency(new HashSet<VNFRecordDependency>());
        nsr2.setVnfr(vnfrSet);
        nsr2.setId("5e074545-0a81-4de8-8494-eb67173ec565");


        nsrSet.add(nsr0);


        vnfr0.setName("iperf-server");
        vnfr0.setId("2135f315-1772-4ad8-85c8-caa209400ef0");
        vnfr0.setEndpoint("generic");
        vnfr0.setDescriptor_reference("f9382f4e-9a42-43c7-bab0-3915719c6fca");
        vnfr0.setVersion("1");
        vnfr0.setVendor("FOKUS");
        vnfr0.setLifecycle_event(leServerSet);

        vnfr1.setName("iperf-client");
        vnfr1.setId("2135f315-1772-4ad8-85c8-caa209400ef0");
        vnfr1.setEndpoint("generic");
        vnfr1.setDescriptor_reference("f9382f4e-9a42-43c7-bab0-3915719c6fca");
        vnfr1.setVersion("1");
        vnfr1.setVendor("FOKUS");
        vnfr1.setLifecycle_event(leClientSet);


        vnfrSet.add(vnfr0);
        vnfrSet.add(vnfr1);


    }




    @Test
    public void nsdCreateExample() throws Exception, BadFormatException {
        when(networkServiceDescriptorManagement.onboard(iperfNsd1)).thenReturn(iperfNsd1);

        this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(iperfNsd1))).
                andExpect(status().isCreated()).
                andDo(document("nsd-create-example", requestFields(
                        fieldWithPath("name").description("The NSD's name"),
                        fieldWithPath("vendor").description("The vendor of the NSD"),
                        fieldWithPath("version").description("The NSD's version"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("An array of <<resources-VirtualNetworkFunctionDescriptor,VirtualNetworkFunctionDescriptors>>"),
                        fieldWithPath("hb_version").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("enabled").description("_")
                ))).
                andDo(document("nsd-create-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void nsdGetAllExample() throws  Exception {
        when(networkServiceDescriptorManagement.query()).thenReturn(nsdSet);


        this.mockMvc.perform(get("/api/v1/ns-descriptors")).
                andExpect(status().isOk()).
                andDo(document("nsd-get-all-example", responseFields(
                        fieldWithPath("[]").description("The array of NetworkServiceDescriptors")
                ))).
                andDo(document("nsd-get-all-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsdGetExample() throws Exception {
        when(networkServiceDescriptorManagement.query("55555c52-f952-430c-b093-45acb2bbf50e")).thenReturn(iperfNsd1);

        iperfNsd1.setId("55555c52-f952-430c-b093-45acb2bbf50e");


        this.mockMvc.perform(get("/api/v1/ns-descriptors/55555c52-f952-430c-b093-45acb2bbf50e")).
                andExpect(status().isOk()).
                andDo(document("nsd-get-example", responseFields(
                        fieldWithPath("auto_scale_policy").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("connection_point").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("enabled").description("_"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("id").description("The NSD's id"),
                        fieldWithPath("monitoring_parameter").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("name").description("The NSD's name"),
                        fieldWithPath("nsd_security").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("pnfd").description("_"),
                        fieldWithPath("service_deployment_flavour").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("vendor").description("The vendor of the NSD"),
                        fieldWithPath("version").description("The NSD's version"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("An array of <<resources-VirtualNetworkFunctionDescriptor, VirtualNetworkFunctionDescriptors>>"),
                        fieldWithPath("vnffgd").type(JsonFieldType.ARRAY).description("_")
                ))).
                andDo(document("nsd-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

        iperfNsd1.setId(null);
    }



    @Test
    public void nsdUpdateExample() throws Exception {
        when(networkServiceDescriptorManagement.update(any(NetworkServiceDescriptor.class))).
                thenReturn(iperfNsd1);

        this.mockMvc.perform(put("/api/v1/ns-descriptors/55555c52-f952-430c-b093-45acb2bbf50e").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(iperfNsd1))).
                andExpect(status().isAccepted()).
                andDo(document("nsd-update-example", requestFields(
                        fieldWithPath("enabled").description("_"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("name").description("The NSD's name"),
                        fieldWithPath("vendor").description("The vendor of the NSD"),
                        fieldWithPath("version").description("The NSD's version"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("An array of <<resources-VirtualNetworkFunctionDescriptor,VirtualNetworkFunctionDescriptors>>")
                ))).
                andDo(document("nsd-update-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void nsdDeleteExample() throws Exception {
        Mockito.doNothing().when(networkServiceDescriptorManagement).delete("55555c52-f952-430c-b093-45acb2bbf50e");

        this.mockMvc.perform(delete("/api/v1/ns-descriptors/55555c52-f952-430c-b093-45acb2bbf50e")).
                andExpect(status().isNoContent());
    }



    @Test
    public void nsdCreateVNFDExample() throws Exception {
        when(networkServiceDescriptorManagement.addVnfd(any(VirtualNetworkFunctionDescriptor.class),
                eq("55555c52-f952-430c-b093-45acb2bbf50e")))
                .thenReturn(iperfVnfdServer);

        this.mockMvc.perform(post("/api/v1/ns-descriptors/55555c52-f952-430c-b093-45acb2bbf50e/vnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(iperfVnfdServer))).
                andExpect(status().isCreated()).
                andDo(document("nsd-create-v-n-f-d-example", requestFields(
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to provide parameters to the Network Service"),
                        fieldWithPath("cyclicDependency").description("_"),
                        fieldWithPath("deployment_flavour").description("The deployment flavour of this VNFD"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("The type of Vnfm in charge of managing this VNF"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("lifecycle_event").type(JsonFieldType.ARRAY).description("The lifecycle events that should be executed on the VNF triggered by the vnfm"),
                        fieldWithPath("name").description("The name of the VNFD"),
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the VNFD"),
                        fieldWithPath("virtual_link").description("Array of VirtualLinks, that represent the type of network connectivity mandated by the VNF vendor between two or more ConnectionPoints"),
                        fieldWithPath("version").description("The version of the VNFD")
                ))).
                andDo(document("nsd-create-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsdGetAllVNFDExample() throws Exception {
        when(networkServiceDescriptorManagement.query("55555c52-f952-430c-b093-45acb2bbf50e")).
                thenReturn(iperfNsd1);

        this.mockMvc.perform(get("/api/v1/ns-descriptors/55555c52-f952-430c-b093-45acb2bbf50e/vnfdescriptors")).
                andExpect(status().isAccepted()).
                andDo(document("nsd-get-all-v-n-f-d-example", responseFields(
                        fieldWithPath("[]").description("The array containing the VNFDs of the NSD with the specified id")
                ))).
                andDo(document("nsd-get-all-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }


    @Test
    public void nsdGetVNFDExample() throws Exception {
        when(networkServiceDescriptorManagement.getVirtualNetworkFunctionDescriptor("55555c52-f952-430c-b093-45acb2bbf50e",
                "faaa910d-457d-4148-b4ea-7373b515febc")).
                thenReturn(iperfVnfdServer);

        this.mockMvc.perform(get("/api/v1/ns-descriptors/55555c52-f952-430c-b093-45acb2bbf50e/vnfdescriptors/faaa910d-457d-4148-b4ea-7373b515febc")).
                andExpect(status().isAccepted()).
                andDo(document("nsd-get-v-n-f-d-example", responseFields(
                        fieldWithPath("auto_scale_policy").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to provide parameters to the Network Service"),
                        fieldWithPath("connection_point").description("_"),
                        fieldWithPath("deployment_flavour").type(JsonFieldType.ARRAY).description("The deployment flavour of this VNFD"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("The type of the Vnfm in charge of managing this VNF"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("id").description("The VNFD's id"),
                        fieldWithPath("lifecycle_event").description("The lifecycle events that should be executed on the VNF triggered by the vnfm"),
                        fieldWithPath("manifest_file").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("manifest_file_security").description("_"),
                        fieldWithPath("monitoring_parameter").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("name").description("The VNFD's name"),
                        fieldWithPath("provides").description("_"),
                        fieldWithPath("requires").description("_"),
                        fieldWithPath("service_deployment_flavour").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vendor").description("The vendor of the VNFD"),
                        fieldWithPath("version").description("The VNFD's version"),
                        fieldWithPath("virtual_link").description("Array of VirtualLinks, that represent the type of network connectivity mandated by the VNF vendor between two or more ConnectionPoints"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnffgd").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("vnfPackage").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vdu_dependency").description("_")
                ))).
                andDo(document("nsd-get-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }




    @Test
    public void nsdUpdateVNFDExample() throws Exception {
        when(networkServiceDescriptorManagement.updateVNF(eq("55555c52-f952-430c-b093-45acb2bbf50e"), eq("faaa910d-457d-4148-b4ea-7373b515febc"),
                any(VirtualNetworkFunctionDescriptor.class))).
                thenReturn(iperfVnfdServer);

        this.mockMvc.perform(put("/api/v1/ns-descriptors/55555c52-f952-430c-b093-45acb2bbf50e/vnfdescriptors/faaa910d-457d-4148-b4ea-7373b515febc").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(iperfVnfdServer))).
                andExpect(status().isAccepted()).
                andDo(document("nsd-update-v-n-f-d-example", requestFields(
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to provide parameters to the Network Service"),
                        fieldWithPath("deployment_flavour").type(JsonFieldType.ARRAY).description("The deployment flavour of this VNFD"),
                        fieldWithPath("cyclicDependency").description("_"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("The type of the Vnfm in charge of managing this VNF"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("lifecycle_event").description("The lifecycle events that should be executed on the VNF triggered by the vnfm"),
                        fieldWithPath("name").description("The name of the VNFD"),
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the VNFD"),
                        fieldWithPath("version").description("The version of the VNFD"),
                        fieldWithPath("virtual_link").description("Array of VirtualLinks, that represent the type of network connectivity mandated by the VNF vendor between two or more ConnectionPoints")
                ))).
                andDo(document("nsd-update-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void nsdDeleteVNFDExample() throws Exception {
        Mockito.doNothing().when(networkServiceDescriptorManagement).
                deleteVnfDescriptor("55555c52-f952-430c-b093-45acb2bbf50e", "faaa910d-457d-4148-b4ea-7373b515febc");

        this.mockMvc.perform(delete("/api/v1/ns-descriptors/55555c52-f952-430c-b093-45acb2bbf50e/vnfdescriptors/faaa910d-457d-4148-b4ea-7373b515febc")).
                andExpect(status().isNoContent());
    }



    @Test
    public void createVNFDExample() throws Exception {
        when(vnfdManagement.add(iperfVnfdClient)).thenReturn(iperfVnfdClient);

        this.mockMvc.perform(post("/api/v1/vnf-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(iperfVnfdClient))).
                andExpect(status().isCreated()).
                andDo(document("create-v-n-f-d-example", requestFields(
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to provide parameters to the Network Service"),
                        fieldWithPath("cyclicDependency").description("_"),
                        fieldWithPath("deployment_flavour").description("The deployment flavour of this VNFD"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("The type of Vnfm in charge of managing this VNF"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("lifecycle_event").type(JsonFieldType.ARRAY).description("The lifecycle events that should be executed on the VNF triggered by the vnfm"),
                        fieldWithPath("name").description("The name of the VNFD"),
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the VNFD"),
                        fieldWithPath("virtual_link").description("Array of VirtualLinks, that represent the type of network connectivity mandated by the VNF vendor between two or more ConnectionPoints"),
                        fieldWithPath("version").description("The version of the VNFD")
                ))).
                andDo(document("create-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void getAllVNFDExample() throws Exception {
        when(vnfdManagement.query()).thenReturn(vnfdSet);

        this.mockMvc.perform(get("/api/v1/vnf-descriptors")).
                andExpect(status().isOk()).
                andDo(document("get-all-v-n-f-d-example", responseFields(
                        fieldWithPath("[]").description("The array containing every VNFD")
                ))).
                andDo(document("get-all-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }


    @Test
    public void getVNFDExample() throws Exception {
        when(vnfdManagement.query("55553c52-f952-430c-b693-45aab2bbf58e")).
                thenReturn(iperfVnfdClient);

        this.mockMvc.perform(get("/api/v1/vnf-descriptors/55553c52-f952-430c-b693-45aab2bbf58e")).
                andExpect(status().isOk()).
                andDo(document("get-v-n-f-d-example", responseFields(
                        fieldWithPath("auto_scale_policy").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to provide parameters to the Network Service"),
                        fieldWithPath("connection_point").description("_"),
                        fieldWithPath("deployment_flavour").type(JsonFieldType.ARRAY).description("The deployment flavour of this VNFD"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("The type of the Vnfm in charge of managing this VNF"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("id").description("The VNFD's id"),
                        fieldWithPath("lifecycle_event").description("The lifecycle events that should be executed on the VNF triggered by the vnfm"),
                        fieldWithPath("manifest_file").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("manifest_file_security").description("_"),
                        fieldWithPath("monitoring_parameter").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("name").description("The VNFD's name"),
                        fieldWithPath("provides").description("_"),
                        fieldWithPath("requires").description("_"),
                        fieldWithPath("service_deployment_flavour").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vendor").description("The vendor of the VNFD"),
                        fieldWithPath("version").description("The VNFD's version"),
                        fieldWithPath("virtual_link").description("Array of VirtualLinks, that represent the type of network connectivity mandated by the VNF vendor between two or more ConnectionPoints"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnffgd").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("vnfPackage").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vdu_dependency").description("_")
                ))).
                andDo(document("get-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }




    @Test
    public void updateVNFDExample() throws Exception {
        when(vnfdManagement.update(any(VirtualNetworkFunctionDescriptor.class), eq("faba910d-449d-4146-b4ea-7373c515aebc"))).
                thenReturn(iperfVnfdClient);

        this.mockMvc.perform(put("/api/v1/vnf-descriptors/faba910d-449d-4146-b4ea-7373c515aebc").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(iperfVnfdClient))).
                andExpect(status().isAccepted()).
                andDo(document("update-v-n-f-d-example", requestFields(
                        fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to provide parameters to the Network Service"),
                        fieldWithPath("deployment_flavour").type(JsonFieldType.ARRAY).description("The deployment flavour of this VNFD"),
                        fieldWithPath("cyclicDependency").description("_"),
                        fieldWithPath("endpoint").type(JsonFieldType.STRING).description("The type of the Vnfm in charge of managing this VNF"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("lifecycle_event").description("The lifecycle events that should be executed on the VNF triggered by the vnfm"),
                        fieldWithPath("name").description("The name of the VNFD"),
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vdu").description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the VNFD"),
                        fieldWithPath("version").description("The version of the VNFD"),
                        fieldWithPath("virtual_link").description("Array of VirtualLinks, that represent the type of network connectivity mandated by the VNF vendor between two or more ConnectionPoints")
                ))).
                andDo(document("update-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void deleteVNFDExample() throws Exception {
        Mockito.doNothing().when(vnfdManagement).
                delete("faba910d-449d-4146-b4ea-7373c515aebc");

        this.mockMvc.perform(delete("/api/v1/vnf-descriptors/faba910d-449d-4146-b4ea-7373c515aebc")).
                andExpect(status().isNoContent());
    }





    @Test
    public void nsrCreateExample() throws Exception, QuotaExceededException, BadFormatException {
        when(networkServiceRecordManagement.onboard(any(NetworkServiceDescriptor.class))).
                thenReturn(nsr0);

        this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(iperfNsd1))).
                andExpect(status().isCreated()).
                andDo(document("nsr-create-example", requestFields(
                        fieldWithPath("enabled").description("_"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("name").description("The name of the NSR"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the NSR"),
                        fieldWithPath("version").description("The version of the NSR"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("Array of the <<resources-VirtualNetworkFunctionDescriptor, VNFDs>> of the NSR"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors")

                ))).
                andDo(document("nsr-create-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsrCreateWithIdExample() throws Exception, QuotaExceededException, BadFormatException {
        when(networkServiceRecordManagement.onboard("c60ec37a-654a-4605-93fb-5a1932db6ecf")).
                thenReturn(nsr0);

        this.mockMvc.perform(post("/api/v1/ns-records/c60ec37a-654a-4605-93fb-5a1932db6ecf")).
                andExpect(status().isCreated());
    }



    @Test
    public void nsrGetAllExample() throws Exception {
        when(networkServiceRecordManagement.query()).thenReturn(nsrSet);

        this.mockMvc.perform(get("/api/v1/ns-records/")).
                andExpect(status().isOk()).
                andDo(document("nsr-get-all-example", responseFields(
                        fieldWithPath("[]").description("The array of NSRs")
                ))).
                andDo(document("nsr-get-all-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsrGetExample() throws Exception {
        when(networkServiceRecordManagement.query("66046d77-aade-4f14-ad39-f2976532e5f2")).thenReturn(nsr0);

        this.mockMvc.perform(get("/api/v1/ns-records/66046d77-aade-4f14-ad39-f2976532e5f2")).
                andExpect(status().isOk()).
                andDo(document("nsr-get-example", responseFields(
                        fieldWithPath("audit_log").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("auto_scale_policy").description("_"),
                        fieldWithPath("connection_point").description("_"),
                        fieldWithPath("descriptor_reference").type(JsonFieldType.STRING).description("The reference to the Network Service Descriptor used to instantiate this Network Service."),
                        fieldWithPath("id").description("The id of the NSR"),
                        fieldWithPath("lifecycle_event").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("lifecycle_event_history").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("monitoring_parameter").description("_"),
                        fieldWithPath("name").description("The name of the NSR"),
                        fieldWithPath("notification").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("pnfr").description("_"),
                        fieldWithPath("resource_reservation").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("runtime_policy_info").type(JsonFieldType.STRING).description("_"),
                        fieldWithPath("service_deployment_flavour").type(JsonFieldType.OBJECT).description("_"),
                        fieldWithPath("status").type(JsonFieldType.OBJECT).description("The status of the NSR"),
                        fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the NSR"),
                        fieldWithPath("version").description("The version of the NSR"),
                        fieldWithPath("vlr").type(JsonFieldType.ARRAY).description("Array of VirtualLinkRecords that record resources used to implement a virtual link"),
                        fieldWithPath("vnf_dependency").type(JsonFieldType.ARRAY).description("Array of <<components-VNFRecordDependency, VNFRecordDependencies>>"),
                        fieldWithPath("vnffgr").description("_"),
                        fieldWithPath("vnfr").type(JsonFieldType.ARRAY).description("An array of <<resources-VirtualNetworkFunctionRecord, VirtualNetworkFunctionRecords>>")
                ))).
                andDo(document("nsr-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsrUpdateExample() throws Exception {
        when(networkServiceRecordManagement.update(any(NetworkServiceRecord.class), eq("66046d77-aade-4f14-ad39-f2976532e5f2"))).
                thenReturn(nsr1Return);

        this.mockMvc.perform(put("/api/v1/ns-records/66046d77-aade-4f14-ad39-f2976532e5f2").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsr1))).
                andExpect(status().isAccepted()).
                andDo(document("nsr-update-example", requestFields(
                        fieldWithPath("descriptor_reference").type(JsonFieldType.STRING).description("The reference to the Network Service Descriptor used to instantiate this Network Service."),
                        fieldWithPath("name").description("The name of the NSR"),
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
        Mockito.doNothing().when(networkServiceRecordManagement).delete("66046d77-aade-4f14-ad39-f2976532e5f2");

        this.mockMvc.perform(delete("/api/v1/ns-records/66046d77-aade-4f14-ad39-f2976532e5f2"))
                .andExpect(status().isNoContent());
    }



    @Test
    public void nsrGetAllVnfrExample() throws Exception {
        when(networkServiceRecordManagement.query("5e074545-0a81-4de8-8494-eb67173ec565")).thenReturn(nsr2);

        this.mockMvc.perform(get("/api/v1/ns-records/5e074545-0a81-4de8-8494-eb67173ec565/vnfrecords/")).
                andExpect(status().isAccepted()).
                andDo(document("nsr-get-all-vnfr-example", responseFields(
                        fieldWithPath("[]").description("The array of NSRs")
                ))).
                andDo(document("nsr-get-all-vnfr-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsrGetVnfrExample() throws Exception {
        when(networkServiceRecordManagement.getVirtualNetworkFunctionRecord("5e074545-0a81-4de8-8494-eb67173ec565",
                "2135f315-1772-4ad8-85c8-caa209400ef0")).thenReturn(vnfr0);

        this.mockMvc.perform(get("/api/v1/ns-records/5e074545-0a81-4de8-8494-eb67173ec565/vnfrecords/2135f315-1772-4ad8-85c8-caa209400ef0")).
                andExpect(status().isAccepted()).
                andDo(document("nsr-get-vnfr-example", responseFields(
                                fieldWithPath("audit_log").type(JsonFieldType.STRING).description("_"),
                                fieldWithPath("auto_scale_policy").description("_"),
                                fieldWithPath("configurations").type(JsonFieldType.OBJECT).description("Configuration object to provide parameters to the Network Service"),
                                fieldWithPath("connected_external_virtual_link").type(JsonFieldType.ARRAY).description("_"),
                                fieldWithPath("connection_point").description("_"),
                                fieldWithPath("descriptor_reference").type(JsonFieldType.STRING).description("The reference to the <<resources-VirtualNetworkFunctionDescriptor, VNFD>> used to instantiate this VNF"),
                                fieldWithPath("deployment_flavour_key").type(JsonFieldType.STRING).description("The key representing the deployment flavour"),
                                fieldWithPath("endpoint").type(JsonFieldType.STRING).description("The type of the Vnfm in charge of managing this VNF"),
                                fieldWithPath("hb_version").description("_"),
                                fieldWithPath("id").description("The id of the VNFR"),
                                fieldWithPath("lifecycle_event").description("The lifecycle events that should be executed on the VNF triggered by the vnfm"),
                                fieldWithPath("lifecycle_event_history").type(JsonFieldType.ARRAY).description("_"),
                                fieldWithPath("localization").type(JsonFieldType.STRING).description("_"),
                                fieldWithPath("monitoring_parameter").type(JsonFieldType.ARRAY).description("_"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("The name of the VNFR"),
                                fieldWithPath("notification").type(JsonFieldType.STRING).description("_"),
                                fieldWithPath("parent_ns_id").type(JsonFieldType.STRING).description("Reference to the <<resources-NetworkServiceRecord, NSRs>> that this VNFR is part of"),
                                fieldWithPath("provides").type(JsonFieldType.OBJECT).description("_"),
                                fieldWithPath("requires").type(JsonFieldType.OBJECT).description("_"),
                                fieldWithPath("runtime_policy_info").type(JsonFieldType.STRING).description("_"),
                                fieldWithPath("status").type(JsonFieldType.OBJECT).description("The status of the VNFR"),
                                fieldWithPath("task").type(JsonFieldType.STRING).description("_"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description("The type of the VNFR"),
                                fieldWithPath("vdu").type(JsonFieldType.ARRAY).description("An aray of <<components-VirtualDeploymentUnit, VirtualDeploymentUnits>>"),
                                fieldWithPath("vendor").type(JsonFieldType.STRING).description("The vendor of the VNFR"),
                                fieldWithPath("version").type(JsonFieldType.STRING).description("The version of the VNFR"),
                                fieldWithPath("virtual_link").type(JsonFieldType.ARRAY).description("Internal Virtual Link instances used in this VNF"),
                                fieldWithPath("vnf_address").type(JsonFieldType.ARRAY).description("An array of addresses (e.g. VLAN, IP) configured for the management access or other internal and external connection interface on this VNF"),
                                fieldWithPath("vnfm_id").type(JsonFieldType.STRING).description("The identification of the <<components-VirtualNetworkFunctionManager, VNFM>> entity managing this VNF"),
                                fieldWithPath("vnfPackage").type(JsonFieldType.OBJECT).description("_")
                ))).
                andDo(document("nsr-get-vnfr-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsrDeleteVnfrExample() throws Exception {
        Mockito.doNothing().when(networkServiceRecordManagement).deleteVNFRecord("5e074545-0a81-4de8-8494-eb67173ec565", "2135f315-1772-4ad8-85c8-caa209400ef0");

        this.mockMvc.perform(delete("/api/v1/ns-records/5e074545-0a81-4de8-8494-eb67173ec565/vnfrecords/2135f315-1772-4ad8-85c8-caa209400ef0")).
                andExpect(status().isNoContent());
    }




    @Test
    public void vimInstanceCreateExample() throws Exception {
        when(vimManagement.add(any(VimInstance.class))).thenReturn(vimInstance0Return);

        this.mockMvc.perform(post("/api/v1/datacenters").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vimInstance0))).
                andExpect(status().isCreated()).
                andDo(document("vim-instance-create-example", requestFields(
                        fieldWithPath("authUrl").description("The key authorisation URL of this PoP"),
                        fieldWithPath("location").description("The location of the data Center"),
                        fieldWithPath("name").description("The name of the VimInstance"),
                        fieldWithPath("password").description("The password"),
                        fieldWithPath("tenant").description("The tenant is a string to refer to a group of users"),
                        fieldWithPath("type").description("The type of the Vim Instance that will start the corresponding plugin"),
                        fieldWithPath("username").description("The user"),
                        fieldWithPath("version").description("The version of the VimInstance")
                ))).
                andDo(document("vim-instance-create-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void vimInstanceGetAllExample() throws Exception {
        when(vimManagement.query()).thenReturn(vimInstanceSet);

        this.mockMvc.perform(get("/api/v1/datacenters")).
                andExpect(status().isOk()).
                andDo(document("vim-instance-get-all-example", responseFields(
                        fieldWithPath("[]").description("The array of VimInstances")
                ))).
                andDo(document("vim-instance-get-all-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void vimInstanceGetExample() throws Exception {
        when(vimManagement.query("07241c3b-9d50-44ba-a495-9d3b96c226bd")).thenReturn(vimInstance0Return);

        this.mockMvc.perform(get("/api/v1/datacenters/07241c3b-9d50-44ba-a495-9d3b96c226bd")).
                andExpect(status().isOk()).
                andDo(document("vim-instance-get-example", responseFields(
                        fieldWithPath("authUrl").description("The key authorisation URL of this PoP"),
                        fieldWithPath("flavours").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("id").description("The id of the VimInstance"),
                        fieldWithPath("images").description("_"),
                        fieldWithPath("keyPair").type(JsonFieldType.STRING).description("The keyPair name stored into OpenStack to get the access to the VMs"),
                        fieldWithPath("location").type(JsonFieldType.OBJECT).description("The location of the data Center"),
                        fieldWithPath("name").description("The name of the VimInstance"),
                        fieldWithPath("networks").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("password").description("The password"),
                        fieldWithPath("securityGroups").type(JsonFieldType.ARRAY).description("_"),
                        fieldWithPath("tenant").description("The tenant is a string to refer to a group of users"),
                        fieldWithPath("type").description("The type of the Vim Instance that will start the corresponding plugin"),
                        fieldWithPath("username").description("The username"),
                        fieldWithPath("version").description("The version of the VimInstance")
                ))).
                andDo(document("vim-instance-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void vimInstanceUpdateExample() throws Exception {
        when(vimManagement.update(any(VimInstance.class), eq("07241c3b-9d50-44ba-a495-9d3b96c226bd"))).
                thenReturn(vimInstance1Return);

        this.mockMvc.perform(put("/api/v1/datacenters/07241c3b-9d50-44ba-a495-9d3b96c226bd").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vimInstance1))).
                andExpect(status().isAccepted()).
                andDo(document("vim-instance-update-example", requestFields(
                        fieldWithPath("authUrl").description("The key authorisation URL of this PoP"),
                        fieldWithPath("location").type(JsonFieldType.OBJECT).description("The location of the data Center"),
                        fieldWithPath("name").description("The name of the VimInstance"),
                        fieldWithPath("password").description("The password"),
                        fieldWithPath("tenant").description("The tenant is a string to refer to a group of users"),
                        fieldWithPath("type").description("The type of the Vim Instance that will start the corresponding plugin"),
                        fieldWithPath("username").description("The username"),
                        fieldWithPath("version").description("The version of the VimInstance")
                ))).
                andDo(document("vim-instance-update-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void vimInstanceDeleteExample() throws Exception {
        Mockito.doNothing().when(vimManagement).delete("07241c3b-9d50-44ba-a495-9d3b96c226bd");

        this.mockMvc.perform(delete("/api/v1/datacenters/07241c3b-9d50-44ba-a495-9d3b96c226bd")).
                andExpect(status().isNoContent());
    }


    @Test
    public void vnfpackageGetAllExample() throws Exception {
        when(vnfPackageManagement.query()).thenReturn(vnfPackageSet);

        this.mockMvc.perform(get("/api/v1/vnf-packages")).
                andExpect(status().isOk()).
                andDo(document("vnfpackage-get-all-example", responseFields(
                        fieldWithPath("[]").description("The Array containing the VNFPackages")
                ))).
                andDo(document("vnfpackage-get-all-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void vnfpackageGetExample() throws Exception {
        when(vnfPackageManagement.query("61a40e7c-a424-4d47-8413-532217ddcf4f")).thenReturn(vnfPackage0);

        this.mockMvc.perform(get("/api/v1/vnf-packages/61a40e7c-a424-4d47-8413-532217ddcf4f")).
                andExpect(status().isOk()).
                andDo(document("vnfpackage-get-example", responseFields(
                        fieldWithPath("id").description("The id of the VNFPackage"),
                        fieldWithPath("image").type(JsonFieldType.OBJECT).description("The image in the VNFPackage used for instantiating the VMs"),
                        fieldWithPath("imageLink").type(JsonFieldType.STRING).description("A link to an image that shall be used for instantiating the VMs"),
                        fieldWithPath("name").description("The name of the VNFPackage"),
                        fieldWithPath("scripts").type(JsonFieldType.ARRAY).description("The scripts that are stored in the VNFPackage"),
                        fieldWithPath("scriptsLink").type(JsonFieldType.STRING).description("A link to a git repository, from which the script files can be downloaded"),
                        fieldWithPath("version").description("The version of the VNFPackage")
                ))).
                andDo(document("vnfpackage-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void vnfpackageUpdateExample() throws Exception {
        when(vnfPackageManagement.update(eq("61a40e7c-a424-4d47-8413-532217ddcf4f"), any(VNFPackage.class))).
                thenReturn(vnfPackage1Return);

        this.mockMvc.perform(put("/api/v1/vnf-packages/61a40e7c-a424-4d47-8413-532217ddcf4f").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfPackage1))).
                andExpect(status().isAccepted()).
                andDo(document("vnfpackage-update-example", requestFields(
                        fieldWithPath("image").description("The image in the VNFPackage used for instantiating the VMs"),
                        fieldWithPath("imageLink").description("A link to an image that shall be used for instantiating the VMs"),
                        fieldWithPath("name").description("The name of the VNFPackage"),
                        fieldWithPath("scriptsLink").description("A link to a git repository, from which the script files can be downloaded"),
                        fieldWithPath("version").description("The version of the VNFPackage")
                ))).
                andDo(document("vnfpackage-update-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void vnfpackageDeleteExample() throws Exception {
        Mockito.doNothing().when(vnfPackageManagement).delete("61a40e7c-a424-4d47-8413-532217ddcf4f");

        this.mockMvc.perform(delete("/api/v1/vnf-packages/61a40e7c-a424-4d47-8413-532217ddcf4f")).
                andExpect(status().isNoContent());
    }


}