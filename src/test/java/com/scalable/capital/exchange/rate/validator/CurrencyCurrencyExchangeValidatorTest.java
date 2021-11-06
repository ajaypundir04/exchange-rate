package com.scalable.capital.exchange.rate.validator;

import com.scalable.capital.exchange.rate.model.CurrencyCurrencyExchangeRequest;
import com.scalable.capital.exchange.rate.service.ECBLookupService;
import com.scalable.capital.exchange.rate.service.ExchangeLookUp;
import com.scalable.capital.exchange.rate.util.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.List;

public class CurrencyCurrencyExchangeValidatorTest {
    private final ConstraintValidatorContext.ConstraintViolationBuilder builder = Mockito
            .mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    private final ConstraintValidatorContext constraintValidatorContext = Mockito
            .mock(ConstraintValidatorContext.class);

    private final ExchangeLookUp exchangeLookUp = Mockito.mock(ECBLookupService.class);
    private final CurrencyExchangeRequestValidator currencyExchangeRequestValidator = new CurrencyExchangeRequestValidator(exchangeLookUp);

    @Before
    public void setUp() {
        currencyExchangeRequestValidationSetup();
        Mockito.when(constraintValidatorContext
                .buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(builder);
    }

    @Test
    public void testCurrencyExchangeRequests() {
        Assert.assertTrue(currencyExchangeRequestValidator.isValid(TestUtil.currencyExchangeRequest(),
                constraintValidatorContext));
    }

    @Test
    public void testCurrencyExchangeRequestsWithFutureDate() {
        CurrencyCurrencyExchangeRequest request = TestUtil.currencyExchangeRequest();
        request.setDate(LocalDate.now().plusDays(1));
        Assert.assertFalse(currencyExchangeRequestValidator.isValid(request,
                constraintValidatorContext));
    }

    @Test
    public void testCurrencyExchangeRequestsWithHistoryDateMoreThan90() {
        CurrencyCurrencyExchangeRequest request = TestUtil.currencyExchangeRequest();
        request.setDate(LocalDate.now().minusDays(91));
        Assert.assertFalse(currencyExchangeRequestValidator.isValid(request,
                constraintValidatorContext));
    }

    @Test
    public void testCurrencyExchangeRequestsWithInvalidCurrency() {
        CurrencyCurrencyExchangeRequest request = TestUtil.currencyExchangeRequest();
        request.setFromCurrency("INV");
        Assert.assertFalse(currencyExchangeRequestValidator.isValid(request,
                constraintValidatorContext));
    }

    private void currencyExchangeRequestValidationSetup() {
        Mockito.when(exchangeLookUp.getCurrencies()).thenReturn(List.of("USD", "EUR"));
    }
}
