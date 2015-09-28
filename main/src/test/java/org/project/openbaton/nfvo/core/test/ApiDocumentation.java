package org.project.openbaton.nfvo.core.test;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.project.openbaton.catalogue.mano.common.*;
import org.project.openbaton.catalogue.mano.descriptor.*;
import org.project.openbaton.catalogue.mano.record.*;
import org.project.openbaton.catalogue.nfvo.Location;
import org.project.openbaton.catalogue.nfvo.NFVImage;
import org.project.openbaton.catalogue.nfvo.VimInstance;
import org.project.openbaton.nfvo.main.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
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



//import org.project.openbaton.nfvo.core.interfaces.VirtualLinkManagement;


/**
 * Created by tbr on 23.09.15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
//@EnableAutoConfiguration
@SpringApplicationConfiguration(classes = Application.class)
//@EnableWebMvc
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

    private NetworkServiceDescriptor nsd, nsd2;

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
        nsd.setPnfd(pnfd);
        nsd.setNsd_security(nsd_security);
        nsd.setName("nsd1");
        nsd.setVersion("1");
        nsd.setVendor("Fokus");
        nsd.setVnffgd(vnffgd);
        nsd.setVld(vld);
        nsd.setService_deployment_flavour(new HashSet<DeploymentFlavour>());
        nsd.setConnection_point(new HashSet<ConnectionPoint>());
        Set<String> monitoringParams = new HashSet<String>();
        monitoringParams.add("monitoringParam1");
        nsd.setMonitoring_parameter(monitoringParams);
        Set<AutoScalePolicy> autoScalePolicies = new HashSet<AutoScalePolicy>();
        AutoScalePolicy asp = new AutoScalePolicy();
        autoScalePolicies.add(asp);
        nsd.setAuto_scale_policy(autoScalePolicies);


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
        nsd2.setConnection_point(new HashSet<ConnectionPoint>());



        nsr.setVersion("2");
        nsr.setName("UpdatedNSR");
        nsr.setVendor("Fokus");
        nsr.setStatus(Status.INACTIVE);
        nsr.setVnfr(new HashSet<VirtualNetworkFunctionRecord>());
        nsr.setVlr(new HashSet<VirtualLinkRecord>());
        nsr.setVnf_dependency(new HashSet<VNFRecordDependency>());
        nsr.setLifecycle_event(new HashSet<LifecycleEvent>());
        nsr.setRuntime_policy_info("RuntimePolicyInfo");

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

    }



    @Test
    public void nsdCreateExample() throws Exception {

        this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andDo(document("nsd-create-example", requestFields(
                        fieldWithPath("auto_scale_policy").description("An array of the NSD's auto scale parameters"),
                        fieldWithPath("connection_point").description("An array containing the NSD's ConnectionPoint"),
                        fieldWithPath("enabled").description("Is the NSD enabled or not"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("monitoring_parameter").description("An array of the NSD's monitoring parameters"),
                        fieldWithPath("name").description("The NSD's name"),
                        fieldWithPath("nsd_security").description("A Security object"),
                        fieldWithPath("pnfd").description("An array of PhysicalNetworkFunctionDescriptors"),
                        fieldWithPath("service_deployment_flavour").
                                description("An array containing the NSD's NetworkServiceDeploymentFlavour"),
                        fieldWithPath("vendor").description("The vendor of the NSD"),
                        fieldWithPath("version").description("The NSD's version"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("An array of VirtualNetworkFunctionDescriptors"),
                        fieldWithPath("vnffgd").description("An array of VNFForwardingGraphDescriptors")
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
                        fieldWithPath("auto_scale_policy").description("An array of the NSD's auto scale parameters"),
                        fieldWithPath("connection_point").description("An array containing the NSD's ConnectionPoint"),
                        fieldWithPath("enabled").description("Is the NSD enabled or not"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("id").description("The NSD's id"),
                        fieldWithPath("monitoring_parameter").description("An array of the NSD's monitoring parameters"),
                        fieldWithPath("name").description("The NSD's name"),
                        fieldWithPath("nsd_security").description("A Security object"),
                        fieldWithPath("pnfd").description("An array of PhysicalNetworkFunctionDescriptors"),
                        fieldWithPath("service_deployment_flavour").description("An array containing the NSD's NetworkServiceDeploymentFlavour"),
                        fieldWithPath("vendor").description("The vendor of the NSD"),
                        fieldWithPath("version").description("The NSD's version"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("An array of VirtualNetworkFunctionDescriptors"),
                        fieldWithPath("vnffgd").description("An array of VNFForwardingGraphDescriptors")
                ))).
                andDo(document("nsd-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }

    @Test
    public void nsdUpdateExample() throws Exception {


        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");


        this.mockMvc.perform(put("/api/v1/ns-descriptors/" + id).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd2))).
                andExpect(status().isAccepted()).
                andDo(document("nsd-update-example", requestFields(
                        fieldWithPath("connection_point").description("An array containing the NSD's ConnectionPoints"),
                        fieldWithPath("enabled").description("Is the NSD enabled or not"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("name").description("The NSD's name"),
                        fieldWithPath("nsd_security").description("A Security object"),
                        fieldWithPath("pnfd").description("An array of PhysicalNetworkFunctionDescriptors"),
                        fieldWithPath("service_deployment_flavour").
                                description("An array containing the NSD's NetworkServiceDeploymentFlavours"),
                        fieldWithPath("vendor").description("The vendor of the NSD"),
                        fieldWithPath("version").description("The NSD's version"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("vnfd").description("An array of VirtualNetworkFunctionDescriptors"),
                        fieldWithPath("vnffgd").description("An array of VNFForwardingGraphDescriptors")
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
                        fieldWithPath("configurations").description("_"),
                        fieldWithPath("connection_point").description("An array containing the VNFD's ConnectionPoints"),
                        fieldWithPath("deployment_flavour").description("The VNFD's DeploymentFlavour"),
                        fieldWithPath("endpoint").description("_"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("id").description("The VNFD's id"),
                        fieldWithPath("lifecycle_event").description("An array of LifecyleEvents"),
                        fieldWithPath("manifest_file").description("_"),
                        fieldWithPath("manifest_file_security").description("_"),
                        fieldWithPath("monitoring_parameter").description("An array of the VNFD's monitoring parameters"),
                        fieldWithPath("name").description("The VNFD's name"),
                        fieldWithPath("provides").description("_"),
                        fieldWithPath("requires").description("_"),
                        fieldWithPath("service_deployment_flavour").
                                description("An array containing the VNFD's NetworkServiceDeploymentFlavours"),
                        fieldWithPath("type").description("The type of the VNFD"),
                        fieldWithPath("vendor").description("The vendor of the VNFD"),
                        fieldWithPath("version").description("The VNFD's version"),
                        fieldWithPath("virtual_link").description("_"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors"),
                        fieldWithPath("vnffgd").description("An array of VNFForwardingGraphDescriptors"),
                        fieldWithPath("vnfPackage").description("_"),
                        fieldWithPath("vdu").description("_"),
                        fieldWithPath("vdu_dependency").description("_")
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
                        fieldWithPath("cyclicDependency").description("_"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("name").description("The name of the VNFD"),
                        fieldWithPath("type").description("The type of the VNFD")
                ))).
                andDo(document("nsd-create-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
        ;

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
                content(gson.toJson(vnfd5))).
                andExpect(status().isAccepted()).
                andDo(document("nsd-update-v-n-f-d-example", requestFields(
                        fieldWithPath("cyclicDependency").description("_"),
                        fieldWithPath("hb_version").description("_"),
                        fieldWithPath("name").description("The name of the VNFD"),
                        fieldWithPath("type").description("The type of the VNFD")
                ))).
                andDo(document("nsd-update-v-n-f-d-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void nsdDeleteVNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
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
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andDo(document("nsr-create-example", requestFields(
                        fieldWithPath("auto_scale_policy").description("_"),
                        fieldWithPath("connection_point").description("_"),
                        fieldWithPath("enabled").description("_"),
                        fieldWithPath("hb_version").description("_"),
                        //fieldWithPath("lifecycle_event").description("An array of LifecycleEvents"),
                        fieldWithPath("monitoring_parameter").description("_"),
                        fieldWithPath("name").description("The name of the NSR"),
                        fieldWithPath("nsd_security").description("_"),
                        fieldWithPath("pnfd").description("_"),
                        //fieldWithPath("runtime_policy_info").description("Contains information related to NS orchestration and management policies to be applied during runtime"),
                        fieldWithPath("service_deployment_flavour").description("_"),
                        //fieldWithPath("status").description("The status of the NSR"),
                        fieldWithPath("vendor").description("The vendor of the NSR"),
                        fieldWithPath("version").description("The version of the NSR"),
                        fieldWithPath("vnf_dependency").description("An array of VNFRecordDependencies"),
                        fieldWithPath("vnfd").description("The VirtualNetworkFunctiondescriptor of the NSR"),
                        fieldWithPath("vnffgd").description("_"),
                        //fieldWithPath("vnfr").description("An array of VirtualNetworkFunctionRecords"),
                        fieldWithPath("vld").description("An array of VirtualLinkDescriptors")
                        //fieldWithPath("vlr").description("A record of the resources used to implement a virtual network link")

                ))).
                andDo(document("nsr-create-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


    @Test
    public void nsrCreateWithIdExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(post("/api/v1/ns-records/" + nsdId)).
                andExpect(status().isCreated());
    }



    @Test
    public void nsrGetAllExample() throws Exception {

        this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated());

        this.mockMvc.perform(get("/api/v1/ns-records/")).
                andExpect(status().isOk()).
                andDo(document("nsr-get-all-example", responseFields(
                        fieldWithPath("[]").description("The array of NSRs")
                ))).
                andDo(document("nsr-get-all-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    // TODO response fields
    @Test
    public void nsrGetExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(get("/api/v1/ns-records/" + nsrId)).
                andExpect(status().isOk()).
                andDo(document("nsr-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }



    @Test
    public void nsrUpdateExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(put("/api/v1/ns-records/" + nsrId).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsr))).
                andExpect(status().isAccepted()).
                andDo(document("nsr-update-example", requestFields(
                        //fieldWithPath("auto_scale_policy").description("_"),
                        fieldWithPath("lifecycle_event").description("An array of LifecycleEvents"),
                        //fieldWithPath("monitoring_parameter").description("_"),
                        fieldWithPath("name").description("The name of the NSR"),
                        fieldWithPath("runtime_policy_info").description("Contains information related to NS orchestration and management policies to be applied during runtime"),
                        fieldWithPath("status").description("The status of the NSR"),
                        fieldWithPath("vendor").description("The vendor of the NSR"),
                        fieldWithPath("version").description("The version of the NSR"),
                        fieldWithPath("vlr").description("A record of the resources used to implement a virtual network link"),
                        fieldWithPath("vnf_dependency").description("An array of VNFRecordDependencies"),
                        fieldWithPath("vnfr").description("An array of VirtualNetworkFunctionRecords")

                        ))).
                andDo(document("nsr-update-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }




//  how can i change the nsr's state???
//    @Test
//    public void nsrDeleteExample() throws Exception {
//        String createResponse = this.mockMvc.perform(post("/api/v1/ns-records").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsd))).
//                andExpect(status().isCreated()).
//                andReturn().getResponse().getContentAsString();
//
//        String nsrId = new JSONObject(createResponse).getString("id");
//
//
//        this.mockMvc.perform(put("/api/v1/ns-records/" + nsrId).
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsr))).
//                andExpect(status().isAccepted());
//
//        this.mockMvc.perform(delete("/api/v1/ns-records/" + nsrId))
//                .andExpect(status().isNoContent());
//    }



    @Test
    public void nsrGetAllVnfrExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

//        this.mockMvc.perform(put("/api/v1/ns-records/" + nsrId).
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsr))).
//                andExpect(status().isAccepted());

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


    // TODO response fields
    @Test
    public void nsrGetVnfrExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-records").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsrId = new JSONObject(createResponse).getString("id");

        String vnfrResponse = this.mockMvc.perform(post("/api/v1/ns-records/" + nsrId + "/vnfrecords/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfr))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String vnfrId = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(get("/api/v1/ns-records/" + nsrId + "/vnfrecords/" + vnfrId)).
                andExpect(status().isAccepted()).
                andDo(document("nsr-get-vnfr-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    }


//  should be working but doesn't
//    @Test
//    public void nsrDeleteVnfrExample() throws Exception {
//        String createResponse = this.mockMvc.perform(post("/api/v1/ns-records").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsd))).
//                andExpect(status().isCreated()).
//                andReturn().getResponse().getContentAsString();
//
//        String nsrId = new JSONObject(createResponse).getString("id");
//
//        String vnfrResponse = this.mockMvc.perform(post("/api/v1/ns-records/" + nsrId + "/vnfrecords/").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(vnfr))).
//                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
//
//        String vnfrId = new JSONObject(createResponse).getString("id");
//
//        this.mockMvc.perform(delete("/api/v1/ns-records/" + nsrId + "/vnfrecords/" + vnfrId)).
//                andExpect(status().isNoContent());
//    }





    @Test
    public void vimInstanceCreateExample() throws Exception {
        this.mockMvc.perform(post("/api/v1/datacenters").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vimInstance))).
                andExpect(status().isCreated()).
                andDo(document("vim-instance-create-example", requestFields(
                        fieldWithPath("authUrl").description("_"),
                        fieldWithPath("flavours").description("_"),
                        fieldWithPath("images").description("An array of Images"),
                        fieldWithPath("location").description("_"),
                        fieldWithPath("name").description("The name of the VimInstance"),
                        fieldWithPath("password").description("_"),
                        fieldWithPath("securityGroups").description("_"),
                        fieldWithPath("tenant").description("_"),
                        fieldWithPath("type").description("The type of the VimInstance"),
                        fieldWithPath("username").description("_"),
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
                        fieldWithPath("authUrl").description("_"),
                        fieldWithPath("flavours").description("_"),
                        fieldWithPath("id").description("The id of the VimInstance"),
                        fieldWithPath("images").description("_"),
                        fieldWithPath("keyPair").description("_"),
                        fieldWithPath("location").description("_"),
                        fieldWithPath("name").description("The name of the VimInstance"),
                        fieldWithPath("networks").description("_"),
                        fieldWithPath("password").description("_"),
                        fieldWithPath("securityGroups").description("_"),
                        fieldWithPath("tenant").description("_"),
                        fieldWithPath("type").description("The type of the VimInstance"),
                        fieldWithPath("username").description("_"),
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
                        fieldWithPath("images").description("An array of Images"),
                        fieldWithPath("name").description("The name of the VimInstance"),
                        fieldWithPath("type").description("The type of the VimInstance"),
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







//    @Test
//    public void NSDCreateVNFDepExample() throws Exception {
//        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsd))).
//                andExpect(status().isCreated()).
//                andReturn().getResponse().getContentAsString();
//
//        String nsdId = new JSONObject(createResponse).getString("id");
//
//        this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(vnfd7)));
//
//        this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/vnfdependencies/").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(vnfdep1))).
//                andExpect(status().isCreated());
//    }



//        @Test
//    public void NSDCreatePNFDExample() throws Exception {
//        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsd))).
//                andExpect(status().isCreated()).
//                andReturn().getResponse().getContentAsString();
//
//        String nsdId = new JSONObject(createResponse).getString("id");
//
//
//        this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(pnfd1))).
//                andExpect(status().isCreated());
//    }
//
//
//    @Test
//    public void NSDGetAllPNFDExample() throws Exception {
//        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsd))).
//                andExpect(status().isCreated()).
//                andReturn().getResponse().getContentAsString();
//
//        String nsdId = new JSONObject(createResponse).getString("id");
//
//
//        this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(pnfd1))).
//                andExpect(status().isCreated());
//
//        this.mockMvc.perform(get("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/")).
//                andExpect(status().isAccepted());
//    }
//
//
//
//    @Test
//    public void NSDGetPNFDExample() throws Exception {
//        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsd))).
//                andExpect(status().isCreated()).
//                andReturn().getResponse().getContentAsString();
//
//        String nsdId = new JSONObject(createResponse).getString("id");
//
//
//        String createPNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(pnfd1))).
//                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
//
//        String pnfdId = new JSONObject(createPNFDResponse).getString("id");
//
//        this.mockMvc.perform(get("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/" + pnfdId)).
//                andExpect(status().isAccepted());
//    }
//
//
//
//    @Test
//    public void NSDUpdatePNFDExample() throws Exception {
//        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsd))).
//                andExpect(status().isCreated()).
//                andReturn().getResponse().getContentAsString();
//
//        String nsdId = new JSONObject(createResponse).getString("id");
//
//
//        String createPNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(pnfd1))).
//                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
//
//        String pnfdId = new JSONObject(createPNFDResponse).getString("id");
//
//        this.mockMvc.perform(put("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/" + pnfdId).
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(pnfd2))).
//                andExpect(status().isAccepted());
//    }
//
//
//
//    @Test
//    public void NSDDeletePNFDExample() throws Exception {
//        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(nsd))).
//                andExpect(status().isCreated()).
//                andReturn().getResponse().getContentAsString();
//
//        String nsdId = new JSONObject(createResponse).getString("id");
//
//
//        String createPNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
//                contentType(MediaType.APPLICATION_JSON_VALUE).
//                content(gson.toJson(pnfd1))).
//                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
//
//        String pnfdId = new JSONObject(createPNFDResponse).getString("id");
//
//        this.mockMvc.perform(delete("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/" + pnfdId)).
//                andExpect(status().isNoContent());
//    }


}