package com.backbase.academy.peachtree.controller;

import com.backbase.academy.peachtree.mapper.ProductSummaryMapper;
import com.backbase.academy.peachtree.service.ProductSummaryService;
import com.backbase.academy.peachtree.util.JsonFileReader;
import com.backbase.buildingblocks.jwt.core.exception.TokenKeyException;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.ProductSummaryGetResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mambu.sdk.api.ApiException;
import com.mambu.sdk.api.v2.ClientsApi;
import com.mambu.sdk.api.v2.DepositAccountsApi;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.ws.rs.core.MediaType;

import static com.backbase.academy.peachtree.util.JsonFileReader.readFromFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(ProductSummaryController.class)
public class ProductSummaryControllerTest {

    static {
        System.setProperty("SIG_SECRET_KEY", "JWTSecretKeyDontUseInProduction!");
    }

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private ProductSummaryController productSummaryController;
    @InjectMocks
    private ProductSummaryService productSummaryService;
    @Mock
    private ProductSummaryMapper productSummaryMapper;
    @Mock
    private ClientsApi clientsApi;
    @Mock
    private DepositAccountsApi depositAccountsApi;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productSummaryController).build();
        try {

            String token = "Basic " + new String(Base64.encodeBase64("bootcamp:peachtree1".getBytes()));
            when(productSummaryService.getProductSummary()).thenReturn(new ProductSummaryGetResponseBody());
        } catch (ApiException | TokenKeyException e) {
            e.printStackTrace();
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @Ignore
    public void getProductSummary() throws Exception {


        ProductSummaryGetResponseBody productSummaryGetResponseBody = JsonFileReader.readFromFile(
                "getProductSummaryCTRL.json", ProductSummaryGetResponseBody.class);

        MvcResult mvcResult = mockMvc.perform(get("/v2/productsummary").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).
                        andExpect(status().isOk()).
                        andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertNotNull("A null response received", responseBody);
        assertFalse("An empty response received", responseBody.isEmpty());


    }
}