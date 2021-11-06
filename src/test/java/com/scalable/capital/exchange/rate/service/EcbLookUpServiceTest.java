package com.scalable.capital.exchange.rate.service;

import com.scalable.capital.exchange.rate.entity.ExchangeRate;
import com.scalable.capital.exchange.rate.util.TestUtil;
import org.junit.*;
import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;

public class EcbLookUpServiceTest {

    private static ClientAndServer mockServer;
    private final CacheManager cacheManager = Mockito.mock(CacheManager.class);
    private final WebClient webClient = Mockito.mock(WebClient.class);
    private final Cache cache = Mockito.mock(Cache.class);
    private final ECBLookupService ecbLookupService = new ECBLookupService(WebClient.create(),
            cacheManager, "http://localhost:1080/current", "http://localhost:1080/history");

    @BeforeClass
    public static void TestSetUp() {
        mockServer = ClientAndServer.startClientAndServer(1080);
    }

    @AfterClass
    public static void cleanUp() {
        mockServer.stop();
    }

    @Before
    public void setUp() throws IOException {
        Mockito.reset(cacheManager, webClient);
    }

    @Test
    public void testGetEurExchangeRate() {

        ExchangeRate exchangeRate = getExchangeRate();
        Assert.assertNotNull(exchangeRate);
        Assert.assertEquals(TestUtil.exchangeRate(LocalDate.parse("2021-03-24")), exchangeRate);
        new MockServerClient("localhost", 1080)
                .verify(
                        request()
                                .withPath("/current"),
                        VerificationTimes.atLeast(1)
                );
    }

    private ExchangeRate getExchangeRate()
    {
        Mockito.when(cacheManager.getCache(Mockito.anyString()))
                .thenReturn(cache);
        Mockito.doNothing().when(cache).put(Mockito.anyString(), Mockito.any(ExchangeRate.class));
        mockServer.when(request("/current")
                .withMethod("GET")
        )
                .respond(HttpResponse.response()
                        .withContentType(MediaType.APPLICATION_XML)
                        .withBody("<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">\n" +
                                "<Cube><Cube time=\"2021-03-24\"><Cube currency=\"USD\" rate=\"1.1825\"/></Cube></Cube></gesmes:Envelope>")
                        .withStatusCode(HttpStatus.OK.value())
                );
        return ecbLookupService.getEurExchangeRate("USD");
    }

    @Test
    public void testGetHistoricalRates() {

        mockServer.when(request("/history")
                .withMethod("GET")
        )
                .respond(HttpResponse.response()
                        .withContentType(MediaType.APPLICATION_XML)
                        .withBody("<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">\n" +
                                "<Cube><Cube time=\"2021-03-24\"><Cube currency=\"USD\" rate=\"1.1825\"/></Cube></Cube></gesmes:Envelope>")
                        .withStatusCode(HttpStatus.OK.value())
                );
        List<ExchangeRate> exchangeRate = ecbLookupService.getHistoricalRates();
        Assert.assertNotNull(exchangeRate);
        Assert.assertEquals(List.of(TestUtil.exchangeRate(LocalDate.parse("2021-03-24"))), exchangeRate);
        new MockServerClient("localhost", 1080)
                .verify(
                        request()
                                .withPath("/history"),
                        VerificationTimes.atLeast(1)
                );
    }

    @Test
    public void testGetCurrencies() {
        getExchangeRate();
        Assert.assertEquals(List.of("USD"), ecbLookupService.getCurrencies());
    }

}
