package com.scalable.capital.exchange.rate.validator;


import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeRequest;
import com.scalable.capital.exchange.rate.service.ExchangeLookUp;
import com.scalable.capital.exchange.rate.util.ExchangeRateUtil;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CurrencyExchangeRequestValidator implements ConstraintValidator<ValidCurrencyExchangeRequest, CurrencyCurrencyExchangeRequest> {

    private final ExchangeLookUp exchangeLookUp;

    public CurrencyExchangeRequestValidator(ExchangeLookUp exchangeLookUp) {
        this.exchangeLookUp = exchangeLookUp;
    }

    @Override
    public boolean isValid(CurrencyCurrencyExchangeRequest value, ConstraintValidatorContext context) {
        List<String> errorMessages = new ArrayList<>();
        context.disableDefaultConstraintViolation();

        if (value.getDate().isAfter(LocalDate.now()) || value.getDate().isBefore(LocalDate.now().minusDays(90))) {
            errorMessages.add(String.format("Exchange Rate for future or more than 90 days ago is not possible %s",
                    value.getDate()));
        }

        if(!ExchangeRateUtil.isValidCurrency(value.getToCurrency(), exchangeLookUp.getCurrencies())
         || !ExchangeRateUtil.isValidCurrency(value.getFromCurrency(), exchangeLookUp.getCurrencies())
        ){
        errorMessages.add(String.format("Exchange Rate requested for unknown currency pair %s - %s",
                    value.getFromCurrency(),
                    Objects.isNull(value.getToCurrency()) ? "EUR" : value.getToCurrency()));
        }
        errorMessages.forEach(e -> context.buildConstraintViolationWithTemplate(e).addConstraintViolation());
        return errorMessages.isEmpty();
    }
}
