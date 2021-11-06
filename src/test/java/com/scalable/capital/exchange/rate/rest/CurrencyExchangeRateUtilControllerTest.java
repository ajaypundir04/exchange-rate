package com.scalable.capital.exchange.rate.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scalable.capital.exchange.rate.exception.GlobalExceptionHandler;
import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeRequest;
import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeResponse;
import com.scalable.capital.exchange.rate.model.Statistics;
import com.scalable.capital.exchange.rate.service.ExchangeRateService;
import com.scalable.capital.exchange.rate.util.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ExchangeRateController.class)
@ContextConfiguration(classes = {ExchangeRateController.class,
        GlobalExceptionHandler.class})
public class CurrencyExchangeRateUtilControllerTest {

    private static final String EXCHANGE_RATE_URL = "/v1/exchange";
    private static final String STATISTICS_URL = "/v1/statistics";
    private static final String PUBLIC_URL = "/v1/public-link";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private ExchangeRateService exchangeRateService;

    @Before
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        Mockito.reset(exchangeRateService);
    }

    @Test
    public void testExchange() throws Exception {
        Mockito.when(exchangeRateService.convert(Mockito.any(CurrencyCurrencyExchangeRequest.class)))
                .thenReturn(TestUtil.currencyExchangeResponse());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(String.join("", EXCHANGE_RATE_URL, "/INR")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Assert.assertNotNull(mvcResult);
        Assert.assertEquals(TestUtil.currencyExchangeResponse(),
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CurrencyCurrencyExchangeResponse.class));

    }


    @Test
    public void testStatistics() throws Exception {
        Mockito.when(exchangeRateService.getStatistics())
                .thenReturn(TestUtil.statistics());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(STATISTICS_URL))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Assert.assertNotNull(mvcResult);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(TestUtil.statistics());
        Statistics actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Statistics.class);
        Statistics expected = objectMapper.readValue(json, Statistics.class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPublicUrl() throws Exception {
        String publicUrl = "https://www.xe.com/currencycharts/?from=USD&to=INR";
        Mockito.when(exchangeRateService.getPublicLink(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(publicUrl);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(String.join("", PUBLIC_URL, "/USD")).param("toCurrency", "INR"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Assert.assertNotNull(mvcResult);
        Assert.assertEquals(publicUrl, mvcResult.getResponse().getContentAsString());
    }


    @Profile("embedded")
    @SpringBootApplication
    public static class Config {

    }

}
