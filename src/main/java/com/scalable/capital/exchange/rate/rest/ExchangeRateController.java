package com.scalable.capital.exchange.rate.rest;

import com.scalable.capital.exchange.rate.model.*;
import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeResponse;
import com.scalable.capital.exchange.rate.model.CurrencyExchange;
import com.scalable.capital.exchange.rate.service.ExchangeRateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Ajay Singh Pundir
 * REST API for all exchange operations
 */
@RestController
@RequestMapping("/v1/")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @ApiOperation(value = "Ecb Exchange Rates")
    @GetMapping(value = "exchange/{fromCurrency}")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    CurrencyCurrencyExchangeResponse convert(@PathVariable(value = "fromCurrency") String fromCurrency, CurrencyExchange currencyExchange) {
        return exchangeRateService.convert(mapTo(fromCurrency, currencyExchange));
    }

    private CurrencyCurrencyExchangeRequest mapTo(String fromCurrency, CurrencyExchange currencyExchange) {
        CurrencyCurrencyExchangeRequest currencyExchangeRequest = new CurrencyCurrencyExchangeRequest();
        currencyExchangeRequest.setToCurrency(currencyExchange.getToCurrency());
        currencyExchangeRequest.setFromCurrency(fromCurrency);
        currencyExchangeRequest.setDate(Objects.isNull(currencyExchange.getDate())?LocalDate.now(): currencyExchange.getDate());
        currencyExchangeRequest.setAmount(currencyExchange.getAmount());
        return currencyExchangeRequest;
    }

    @ApiOperation(value = "Exchange Api Statistics")
    @GetMapping(value = "statistics")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    Statistics getStatistics() {
        return exchangeRateService.getStatistics();
    }

    @ApiOperation(value = "Public Link")
    @GetMapping(value = "public-link/{fromCurrency}")
    @ResponseStatus(value = HttpStatus.OK)
    public String getPublicLink(@PathVariable ("fromCurrency") String fromCurrency,
                                @RequestParam(value = "toCurrency", required = false) String toCurrency) {
        return exchangeRateService.getPublicLink(fromCurrency, toCurrency);
    }

}
