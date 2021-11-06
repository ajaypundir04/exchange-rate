package com.scalable.capital.exchange.rate.service;

import com.scalable.capital.exchange.rate.entity.ExchangeRate;
import com.scalable.capital.exchange.rate.exception.ExchangeRateException;
import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeRequest;
import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeResponse;
import com.scalable.capital.exchange.rate.model.Statistics;
import com.scalable.capital.exchange.rate.util.ExchangeRateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Ajay Singh Pundir
 * Handles all the operation for exchange rate
 */
@Service
@Validated
public class ExchangeRateService {

    private final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateService.class);
    private final ExchangeLookUp exchangeLookUp;
    private final Map<Pair<String, String>, AtomicInteger> currencyPairCountMap;
    private final Map<String, AtomicInteger> currencyCountMap;
    private final String publicUrl;

    @Autowired
    public ExchangeRateService(ExchangeLookUp exchangeLookUp,
                               @Value("${public.url}") String publicUrl) {
        this.exchangeLookUp = exchangeLookUp;
        this.currencyPairCountMap = new ConcurrentHashMap<>();
        this.currencyCountMap = new ConcurrentHashMap<>();
        this.publicUrl = publicUrl;
    }

    /**
     * populating caches for the exchange rates.
     */
    @PostConstruct
    public void populateCache() {
        exchangeLookUp.getEurExchangeRate("INR");
    }

    /**
     * Conversion of currency.
     *
     * @param currencyExchangeRequest @{@link CurrencyCurrencyExchangeRequest} holds exchange request
     * @return @{@link CurrencyCurrencyExchangeResponse} response after conversion
     */
    public CurrencyCurrencyExchangeResponse convert(@Valid @NotNull CurrencyCurrencyExchangeRequest currencyExchangeRequest) {
        double fromExchangeRateVal = 1f;
        double toExchangeRateVal = 1f;
        if( Objects.isNull(currencyExchangeRequest.getToCurrency()) ){
            currencyExchangeRequest.setToCurrency("EUR");
        }
        updateExchangeCounter(currencyExchangeRequest.getFromCurrency(), currencyExchangeRequest.getToCurrency());
        LocalDate businessDate = currencyExchangeRequest.getDate().equals(LocalDate.now()) ? LocalDate.now() :
                calculateBusinessDate(currencyExchangeRequest.getDate());

        fromExchangeRateVal = getExchangeRateValue(businessDate, currencyExchangeRequest.getFromCurrency(), fromExchangeRateVal);
        toExchangeRateVal = getExchangeRateValue(businessDate, currencyExchangeRequest.getToCurrency(), toExchangeRateVal);
        CurrencyCurrencyExchangeResponse response = mapTo(currencyExchangeRequest);
        response.setRate(toExchangeRateVal / fromExchangeRateVal);
        if (Objects.nonNull(currencyExchangeRequest.getAmount()))
        {
            response.setAmount(currencyExchangeRequest.getAmount()
                    .multiply(BigDecimal.valueOf(response.getRate())));
        }
        return response;
    }

    private void updateExchangeCounter(String fromCurrency, String toCurrency) {
        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();
        Pair<String,String> currencyPair = Pair.of(fromCurrency, toCurrency);
        this.currencyPairCountMap.computeIfAbsent(currencyPair, k -> new AtomicInteger(0));
        this.currencyCountMap.computeIfAbsent(fromCurrency, k -> new AtomicInteger(0));
        this.currencyCountMap.computeIfAbsent(toCurrency, k -> new AtomicInteger(0));
        this.currencyPairCountMap.computeIfPresent(currencyPair, (k, v) -> {
            v.getAndIncrement();
            return v;
        });
        this.currencyCountMap.computeIfPresent(fromCurrency, (k, v) -> {
            v.getAndIncrement();
            return v;
        });
        this.currencyCountMap.computeIfPresent(toCurrency, (k, v) -> {
                    v.getAndIncrement();
                    return v;
                }
        );

    }

    private double getExchangeRateValue(LocalDate date, String currency, double exchangeRateVal) {

        if (!"EUR".equalsIgnoreCase(currency)) {

            ExchangeRate exchangeRate = date.compareTo(LocalDate.now()) == 0 ? exchangeLookUp.getEurExchangeRate(currency)
                    : getRate(exchangeLookUp.getHistoricalRates(), currency, date);
            exchangeRateVal = exchangeRate.getRate();
        }
        LOGGER.debug(String.format("Exchange Rate for %s-EUR on %s is %s", currency, date, exchangeRateVal));
        return exchangeRateVal;
    }

    private ExchangeRate getRate(Collection<ExchangeRate> rates, String currency, LocalDate date) {


        return rates.stream()
                .filter(s -> currency.equalsIgnoreCase(s.getCurrency()) && (date.equals(LocalDate.now()) || date.equals(s.getDate())))
                .findFirst().orElseThrow(() -> new ExchangeRateException("Exchange Rate Not Found"));
    }

    private CurrencyCurrencyExchangeResponse mapTo(CurrencyCurrencyExchangeRequest exchangeRequest) {
        CurrencyCurrencyExchangeResponse response = new CurrencyCurrencyExchangeResponse();
        response.setFromCurrency(exchangeRequest.getFromCurrency());
        response.setToCurrency(exchangeRequest.getToCurrency());
        response.setDate(exchangeRequest.getDate());
        return response;
    }

    private LocalDate calculateBusinessDate(LocalDate businessDate) {
        List<LocalDate> businessDays = exchangeLookUp.getHistoricalRates()
                .stream()
                .map(ExchangeRate::getDate)
                .collect(Collectors.toList());

        while (!businessDays.contains(businessDate)) {
            businessDate = businessDate.plusDays(1);
        }
        return businessDate;
    }

    /**
     * It will calculate the statistics for the exchange rates.
     *
     * @return @{@link Statistics} statistics for exchange.
     */
    public Statistics getStatistics() {
        Statistics statistics = new Statistics();
        statistics.getCurrencyCountMap().putAll(
                this.currencyCountMap.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()))
        );
        statistics.getCurrencyPairCountMap().putAll(
                this.currencyPairCountMap.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()))
        );
        statistics.setTotalCurrencies(exchangeLookUp.getCurrencies().size());
        exchangeLookUp.getCurrencies().forEach(curr -> statistics.getCurrencyCountMap().computeIfAbsent(curr, k -> 0));
        return statistics;
    }

    /**
     * Public url to retrieve the charts of the currency pair
     *
     * @param fromCurrency conversion from
     * @param toCurrency   conversion to
     * @return public url
     */
    public String getPublicLink(String fromCurrency, String toCurrency) {
        if (!ExchangeRateUtil.isValidCurrency(fromCurrency, exchangeLookUp.getCurrencies()) ||
            !ExchangeRateUtil.isValidCurrency(toCurrency, exchangeLookUp.getCurrencies())
        ) {
            throw  new ExchangeRateException(String.format("Exchange Rate requested for " +
                    "unknown currency pair %s - %s", fromCurrency, toCurrency));
        }
        return String.format(this.publicUrl, fromCurrency.toUpperCase(),
                Objects.isNull(toCurrency) ? "EUR" : toCurrency.toUpperCase());
    }
}
