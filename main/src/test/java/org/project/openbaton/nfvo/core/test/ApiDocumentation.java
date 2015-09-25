package org.project.openbaton.nfvo.core.test;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.project.openbaton.catalogue.mano.common.Security;
import org.project.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.project.openbaton.catalogue.mano.descriptor.PhysicalNetworkFunctionDescriptor;
import org.project.openbaton.catalogue.mano.descriptor.VNFDependency;
import org.project.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
import org.project.openbaton.nfvo.main.Application;
import org.project.openbaton.nfvo.repositories.NetworkServiceDescriptorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sound.midi.Receiver;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private ApplicationContext applicationContext;

    @Autowired
    private NetworkServiceDescriptorRepository networkServiceDescriptorRepository;


    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;


    private NetworkServiceDescriptor nsd, nsd2;

    private Set< VirtualNetworkFunctionDescriptor> vnfd = new HashSet<VirtualNetworkFunctionDescriptor>();


    private VirtualNetworkFunctionDescriptor vnfd1, vnfd2, vnfd3, vnfd4, vnfd5, vnfd6, vnfd7;

    private VNFDependency vnfdep1;

    private PhysicalNetworkFunctionDescriptor pnfd1, pnfd2;



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
        nsd.setVnfd(vnfd);
        nsd.setVnf_dependency(vnf_dependency);
        nsd.setPnfd(pnfd);
        nsd.setNsd_security(nsd_security);


        nsd2 = new NetworkServiceDescriptor();
        Security nsd_security2 = new Security();
        nsd_security2.setVersion(2);
        nsd2.setVnfd(vnfd);
        nsd2.setVnf_dependency(vnf_dependency);
        nsd2.setPnfd(pnfd);
        nsd2.setNsd_security(nsd_security2);

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

    }



    @Test
    public void NSDCreateExample() throws Exception {
        this.document.snippets(
                requestFields(
                        fieldWithPath("vnfd").description("An array of VirtualNetworkFunctionDescriptors"),
                        fieldWithPath("vnf_dependency").description("An array of VNFDependencies"),
                        fieldWithPath("pnfd").description("An array of PhysicalNetworkFunctionDescriptors"),
                        fieldWithPath("nsd_security").description("A Security object"),
                        fieldWithPath("enabled").description("Optional ???"),
                        fieldWithPath("hb_version").description("???")
                ),
                links()
        );


        this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated());
    }



    @Test
    public void NSDDeleteExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");

        this.mockMvc.perform(delete("/api/v1/ns-descriptors/" + id)).
                andExpect(status().isNoContent());
    }


    @Test
    public void NSDGetAllExample() throws  Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");

        this.document.snippets(
                responseFields(fieldWithPath("[]").description("The Array of NetworkServiceDescriptors"))
        );

        this.mockMvc.perform(get("/api/v1/ns-descriptors")).
                andExpect(status().isOk());
    }


    @Test
    public void NSDGetExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");

        this.document.snippets(
                responseFields(fieldWithPath("vnfd").description("An array of VirtualNetworkFunctionDescriptors"),
                        fieldWithPath("id").description("The NetworkServiceDescriptor's id"),
                        fieldWithPath("name").description("The NetworkServiceDescriptor's name"),
                        fieldWithPath("vnf_dependency").description("???"),
                        fieldWithPath("pnfd").description("An array of PhysicalNetworkFunctionDescriptors"),
                        fieldWithPath("nsd_security").description("The Security object ???"),
                        fieldWithPath("hb_version").description("???"),
                        fieldWithPath("vendor").description("The NetworkServiceDescriptor's vendor"),
                        fieldWithPath("version").description("The NetworkServiceDescriptor's version"),
                        fieldWithPath("vnffgd").description("The NSD's VNFForwardingGraphDescriptor"),
                        fieldWithPath("vld").description("The NetworkServiceDescriptor's VirtualLinkDescriptor"),
                        fieldWithPath("monitoring_parameter").description("???"),
                        fieldWithPath("service_deployment_flavour").description("The NetworkServiceDescriptor's ServiceDeploymentFlavour"),
                        fieldWithPath("auto_scale_policy").description("The NetworkServiceDescriptor's AutoScalePolicy"),
                        fieldWithPath("connection_point").description("The NetworkServiceDescriptor's ConnectionPoint"),
                        fieldWithPath("enabled").description("???"))
                );

        this.mockMvc.perform(get("/api/v1/ns-descriptors/" + id)).
                andExpect(status().isOk());
    }

    @Test
    public void NSDUpdateExample() throws Exception {

        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String id = new JSONObject(createResponse).getString("id");


        this.mockMvc.perform(put("/api/v1/ns-descriptors/" + id).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd2))).
                andExpect(status().isAccepted());
    }





    @Test
    public void NSDGetVNFDExample() throws  Exception {

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
                andExpect(status().isAccepted());

//        this.mockMvc.perform(delete("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/" + vnfdId));
    }



    @Test
    public void NSDGetAllVNFDExample() throws Exception {

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
                andExpect(status().isAccepted());

    }



    @Test
    public void NSDCreateVNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/vnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(vnfd3))).
                andExpect(status().isCreated());

    }



    @Test
    public void NSDUpdateVNFDExample() throws Exception {
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
                andExpect(status().isAccepted());
    }


    @Test
    public void NSDDeleteVNFDExample() throws Exception {
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



        @Test
    public void NSDCreatePNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(pnfd1))).
                andExpect(status().isCreated());
    }


    @Test
    public void NSDGetAllPNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(pnfd1))).
                andExpect(status().isCreated());

        this.mockMvc.perform(get("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/")).
                andExpect(status().isAccepted());
    }



    @Test
    public void NSDGetPNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        String createPNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(pnfd1))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String pnfdId = new JSONObject(createPNFDResponse).getString("id");

        this.mockMvc.perform(get("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/" + pnfdId)).
                andExpect(status().isAccepted());
    }



    @Test
    public void NSDUpdatePNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        String createPNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(pnfd1))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String pnfdId = new JSONObject(createPNFDResponse).getString("id");

        this.mockMvc.perform(put("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/" + pnfdId).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(pnfd2))).
                andExpect(status().isAccepted());
    }



    @Test
    public void NSDDeletePNFDExample() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(nsd))).
                andExpect(status().isCreated()).
                andReturn().getResponse().getContentAsString();

        String nsdId = new JSONObject(createResponse).getString("id");


        String createPNFDResponse = this.mockMvc.perform(post("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/").
                contentType(MediaType.APPLICATION_JSON_VALUE).
                content(gson.toJson(pnfd1))).
                andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        String pnfdId = new JSONObject(createPNFDResponse).getString("id");

        this.mockMvc.perform(delete("/api/v1/ns-descriptors/" + nsdId + "/pnfdescriptors/" + pnfdId)).
                andExpect(status().isNoContent());
    }


}