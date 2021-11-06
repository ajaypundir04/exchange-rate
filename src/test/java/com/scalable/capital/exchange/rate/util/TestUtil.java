package com.scalable.capital.exchange.rate.util;

import com.scalable.capital.exchange.rate.entity.ExchangeRate;
import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeRequest;
import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeResponse;
import com.scalable.capital.exchange.rate.model.Statistics;
import com.scalable.capital.exchange.rate.model.ecb.Cube;
import com.scalable.capital.exchange.rate.model.ecb.Envelope;
import com.scalable.capital.exchange.rate.model.ecb.TimeCube;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class TestUtil {
    private TestUtil() {
    }

    public static CurrencyCurrencyExchangeResponse currencyExchangeResponse() {
        CurrencyCurrencyExchangeResponse response = new CurrencyCurrencyExchangeResponse();
        response.setAmount(new BigDecimal("44"));
        response.setRate(1.0);
        response.setFromCurrency("INR");
        response.setToCurrency("EUR");
        response.setDate(LocalDate.now());
        return response;
    }

    public static Statistics statistics() {
        Statistics statistics = new Statistics();
        statistics.setTotalCurrencies(1);
        statistics.getCurrencyCountMap().putAll(Map.of("USD", 1, "EUR", 1));
        statistics.getCurrencyPairCountMap().putAll(Map.of(Pair.of("USD", "EUR"), 1));
        return statistics;
    }


    public static Envelope envelope() {
        Envelope envelope = new Envelope();
        TimeCube timeCube = new TimeCube();
        timeCube.setTime(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Cube cube = new Cube();
        cube.setCurrency("INR");
        cube.setRate(86.10);
        timeCube.setTimeCubes(List.of(cube));
        envelope.setTimeCubes(List.of(timeCube));
        return envelope;
    }

    public static ExchangeRate exchangeRate(LocalDate date) {
        return new ExchangeRate("USD", 1.1825, date);
    }

    public static CurrencyCurrencyExchangeRequest currencyExchangeRequest() {
        CurrencyCurrencyExchangeRequest request = new CurrencyCurrencyExchangeRequest();
        request.setAmount(new BigDecimal(4.0));
        request.setFromCurrency("USD");
        request.setToCurrency("EUR");
        request.setDate(LocalDate.now());
        return request;
    }
}
