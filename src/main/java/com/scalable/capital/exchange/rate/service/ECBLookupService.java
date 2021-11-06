package com.scalable.capital.exchange.rate.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.scalable.capital.exchange.rate.entity.ExchangeRate;
import com.scalable.capital.exchange.rate.exception.EcbResponseException;
import com.scalable.capital.exchange.rate.exception.ExchangeRateException;
import com.scalable.capital.exchange.rate.model.ecb.Cube;
import com.scalable.capital.exchange.rate.model.ecb.Envelope;
import com.scalable.capital.exchange.rate.model.ecb.TimeCube;
import com.scalable.capital.exchange.rate.util.ExchangeRateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ajay Singh Pundir
 * It will be used for the interaction with ECB reference rate
 */
@Service
public class ECBLookupService implements ExchangeLookUp {

    private final Logger LOGGER = LoggerFactory.getLogger(ECBLookupService.class);
    private final WebClient webClient;

    private final CacheManager cacheManager;
    private final String currentUrl;
    private final String historyUrl;
    private List<String> currencies;

    @Autowired
    public ECBLookupService(WebClient webClient,
                            CacheManager cacheManager,
                            @Value("${ecb.api.current}") String currentUrl,
                            @Value("${ecb.api.history}") String historyUrl) {
        this.webClient = webClient;
        this.cacheManager = cacheManager;
        this.currentUrl = currentUrl;
        this.historyUrl = historyUrl;
    }

    /**
     * Getting the historical exchange rates.
     *
     * @return List @{@link com.scalable.capital.exchange.rate.entity.ExchangeRate} rates for conversion
     */
    @Cacheable(value = "ecbHistoricalExchangeRate")
    public List<ExchangeRate> getHistoricalRates() {
        LOGGER.info("Getting historical exchange rates");
        return getRates(false);
    }

    /**
     * Getting latest exchange rate.
     *
     * @param currency currency for getting rates
     * @return @{@link com.scalable.capital.exchange.rate.entity.ExchangeRate} rate for conversion
     */
    @Cacheable(cacheNames = ExchangeRateUtil.ECB_EXCHANGE_RATE)
    public ExchangeRate getEurExchangeRate(String currency) {
        LOGGER.info("Getting latest exchange rates");
        return fetchAndRefreshExchangeRate(currency);
    }

    /**
     * Return list of supported currencies
     * @return @{@link List} of currencies
     */
    public List<String> getCurrencies() {
        return Collections.unmodifiableList(this.currencies);
    }

    private ExchangeRate fetchAndRefreshExchangeRate(String baseCurrency) {
        List<ExchangeRate> exchangeRates = getRates(true);
        this.currencies = exchangeRates.stream().map(e -> e.getCurrency()).collect(Collectors.toList());
        Cache ecbExchangeRate = cacheManager.getCache(ExchangeRateUtil.ECB_EXCHANGE_RATE);
        exchangeRates.forEach(e -> ecbExchangeRate.put(e.getCurrency(), e));
        return exchangeRates.stream().filter(e -> e.getCurrency().equalsIgnoreCase(baseCurrency))
                .findFirst()
                .orElseThrow(() -> new ExchangeRateException("Exchange rate for currency not found."));
    }

    private List<ExchangeRate> getRates(boolean isCurrent) {
        try {
            LOGGER.debug("getting Envelope");
            Envelope envelope = getEnvelope(isCurrent);

            LOGGER.debug("transform Envelope into collection of ExchangeRate");
            return transformEnvelopeToExchangeRates(envelope);
        } catch (Exception e) {
            String errorMessage = "Exception while processing Ecb Response";
            LOGGER.error(errorMessage, e);
            throw new EcbResponseException(errorMessage, e);
        }
    }

    private List<ExchangeRate> transformEnvelopeToExchangeRates(Envelope envelope) throws Exception {
        LinkedList<ExchangeRate> rates = new LinkedList<>();

        List<TimeCube> timeCubes = envelope.getTimeCubes();
        timeCubes.stream().forEach(timeCube -> transformTimeCube(timeCube, rates));

        return rates;
    }

    private void transformTimeCube(TimeCube timeCube, LinkedList<ExchangeRate> rates) {
        LocalDate date = parseDate(timeCube);
        timeCube.getTimeCubes().stream().forEach(cube -> transformCube(cube, date, rates));
    }

    private LocalDate parseDate(TimeCube timeCube) {
        String timeString = timeCube.getTime();

        try {
            return LocalDate.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            LOGGER.error("Cannot parse date: " + timeString, e);
            return null;
        }

    }

    private void transformCube(Cube cube, LocalDate date, LinkedList<ExchangeRate> rates) {
        ExchangeRate exchangeRate = new ExchangeRate(cube.getCurrency(), cube.getRate(), date);
        rates.add(exchangeRate);
    }

    private Envelope getEnvelope(Boolean isCurrent) throws JsonProcessingException {
        return webClient
                .method(HttpMethod.GET)
                .uri(isCurrent ? currentUrl : historyUrl)
                .contentType(MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(s -> s != HttpStatus.OK, clientResponse ->
                        clientResponse.bodyToMono(ByteArrayResource.class)
                                .map(ByteArrayResource::getByteArray)
                                .map(String::new)
                                .map(ExchangeRateException::new)
                )
                .bodyToMono(String.class)
                .map(xml -> {
                    try {
                        return new XmlMapper().readValue(xml, Envelope.class);
                    } catch (JsonProcessingException je) {
                        String errorMessage = "Exception while processing ecb response";
                        LOGGER.error(errorMessage, je);
                        throw new EcbResponseException(errorMessage, je);
                    }

                })
                .block();
    }

}