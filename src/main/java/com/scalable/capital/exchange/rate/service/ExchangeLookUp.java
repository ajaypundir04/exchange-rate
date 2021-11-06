package com.scalable.capital.exchange.rate.service;

import com.scalable.capital.exchange.rate.entity.ExchangeRate;

import java.util.List;

/**
 * @author Ajay Singh Pundir
 * It holds the operation for interacting with the external exchange api.
 */
public interface ExchangeLookUp {

    List<ExchangeRate> getHistoricalRates();

    ExchangeRate getEurExchangeRate(String baseCurrency);

    List<String> getCurrencies();

}
