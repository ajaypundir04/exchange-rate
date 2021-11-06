package com.scalable.capital.exchange.rate.model;

import com.scalable.capital.exchange.rate.validator.ValidCurrencyExchangeRequest;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@ValidCurrencyExchangeRequest
public class CurrencyCurrencyExchangeRequest extends CurrencyExchange {
    @NotNull
    private String fromCurrency;


    public CurrencyCurrencyExchangeRequest(String toCurrency, LocalDate date,
                                           BigDecimal amount, @NotNull String fromCurrency) {
        super(toCurrency, date, amount);
        this.fromCurrency = fromCurrency;
    }


    public CurrencyCurrencyExchangeRequest() {
        super();
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyCurrencyExchangeRequest that = (CurrencyCurrencyExchangeRequest) o;
        return Objects.equals(fromCurrency, that.fromCurrency)
                && Objects.equals(super.getToCurrency(), that.getToCurrency())
                && Objects.equals(super.getAmount(), that.getAmount())
                && Objects.equals(super.getDate(), that.getDate());

    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCurrency, super.getToCurrency(),
                super.getAmount(), super.getDate());
    }

    @Override
    public String toString() {
        return "CurrencyExchangeRequest{" +
                "currencyFrom='" + fromCurrency + '\'' +
                ", currencyTo='" + super.getToCurrency() + '\'' +
                ", amount=" + super.getAmount() +
                ", date=" + super.getDate() +
                '}';
    }
}

