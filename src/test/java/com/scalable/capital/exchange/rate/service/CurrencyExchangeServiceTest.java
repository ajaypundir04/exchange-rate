package com.scalable.capital.exchange.rate.service;

import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeResponse;
import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeRequest;
import com.scalable.capital.exchange.rate.model.Statistics;
import com.scalable.capital.exchange.rate.util.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CurrencyExchangeServiceTest {

    private final ECBLookupService exchangeLookUp = Mockito.mock(ECBLookupService.class);
    private final ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeLookUp,
            "https://www.xe.com/currencycharts/?from=%s&to=%s");

    @Before
    public void setUp() {
        Mockito.reset(exchangeLookUp);
    }

    @Test
    public void testPopulateCache() {
        exchangeRateService.populateCache();
        Mockito.verify(exchangeLookUp, Mockito.times(1))
                .getEurExchangeRate(Mockito.anyString());
    }

    @Test
    public void testConvertCurrent() {
        CurrencyCurrencyExchangeResponse response = getConversionResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(currencyExchangeResponse(), response);
    }

    private CurrencyCurrencyExchangeResponse getConversionResponse() {
        Mockito.when(exchangeLookUp.getEurExchangeRate(Mockito.anyString()))
                .thenReturn(TestUtil.exchangeRate(LocalDate.now()));
        CurrencyCurrencyExchangeRequest currencyExchangeRequest = TestUtil.currencyExchangeRequest();
        return exchangeRateService.convert(currencyExchangeRequest);
    }

    @Test
    public void testConvertHistorical() {
        Mockito.when(exchangeLookUp.getHistoricalRates())
                .thenReturn(List.of(TestUtil.exchangeRate(LocalDate.now().minusDays(1))));
        CurrencyCurrencyExchangeRequest currencyExchangeRequest = TestUtil.currencyExchangeRequest();
        currencyExchangeRequest.setDate(LocalDate.now().minusDays(1));
        CurrencyCurrencyExchangeResponse response = exchangeRateService.convert(currencyExchangeRequest);
        Assert.assertNotNull(response);
        CurrencyCurrencyExchangeResponse expected = currencyExchangeResponse();
        expected.setDate(expected.getDate().minusDays(1));
        Assert.assertEquals(expected, response);
    }

    @Test
    public void testStatistics() {
        getConversionResponse();
        Mockito.when(exchangeLookUp.getCurrencies()).thenReturn(List.of("USD"));
        Statistics statistics = exchangeRateService.getStatistics();
        Assert.assertEquals(TestUtil.statistics(), statistics);
    }

    @Test
    public void testPublicLink() {
        Mockito.when(exchangeLookUp.getCurrencies()).thenReturn(List.of("USD"));
        String publicUrl = exchangeRateService.getPublicLink("USD","EUR");
        Assert.assertEquals("https://www.xe.com/currencycharts/?from=USD&to=EUR",
                publicUrl);
    }

    private CurrencyCurrencyExchangeResponse currencyExchangeResponse() {
        CurrencyCurrencyExchangeResponse response = new CurrencyCurrencyExchangeResponse();
        response.setDate(LocalDate.now());
        response.setToCurrency("EUR");
        response.setFromCurrency("USD");
        response.setRate(0.8456659619450316);
        response.setAmount(new BigDecimal("3.3826638477801264"));
        return response;
    }

}
