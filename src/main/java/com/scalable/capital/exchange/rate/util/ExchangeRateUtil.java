package com.scalable.capital.exchange.rate.util;

import java.util.List;

public class ExchangeRateUtil {
    private ExchangeRateUtil(){}
    public static final String DATE_ATTRIBUTE_FORMAT = "yyyy-MM-dd";
    public static final String ECB_EXCHANGE_RATE = "ecbExchangeRate";
    public static final String ECB_BASE_CURRENCY = "EUR";

    public static boolean isValidCurrency(String currency, List<String> validCurrencies){
        return ECB_BASE_CURRENCY.equalsIgnoreCase(currency) || validCurrencies.contains(currency.toUpperCase()) ;
    }
}
