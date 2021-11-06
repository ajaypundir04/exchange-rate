package com.scalable.capital.exchange.rate.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@JsonPropertyOrder({ "fromCurrency", "toCurrency",
        "date", "rate", "amount" })
public class CurrencyCurrencyExchangeResponse extends CurrencyExchange {

    private String fromCurrency;

    private Double rate;

    public CurrencyCurrencyExchangeResponse() {
        super();
    }

    public CurrencyCurrencyExchangeResponse(String toCurrency, LocalDate date,
                                            BigDecimal amount, String fromCurrency, Double rate) {
        super(toCurrency, date, amount);
        this.fromCurrency = fromCurrency;
        this.rate = rate;
    }



    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return super.getToCurrency();
    }

    public void setToCurrency(String toCurrency) {
        super.setToCurrency(toCurrency);
    }

    public Double getRate() {
        return this.rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return super.getAmount();
    }

    public void setAmount(BigDecimal amount) {
        super.setAmount(amount);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyCurrencyExchangeResponse that = (CurrencyCurrencyExchangeResponse) o;
        return Objects.equals(fromCurrency, that.fromCurrency)
                && Objects.equals(super.getToCurrency(), that.getToCurrency())
                && Objects.equals(rate, that.rate)
                && Objects.equals(super.getAmount(), that.getAmount())
                && Objects.equals(super.getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCurrency, super.getToCurrency(),
                rate, super.getAmount(), super.getDate());
    }

    @Override
    public String toString() {
        return "CurrencyExchangeResponse{" +
                "currencyFrom='" + fromCurrency + '\'' +
                ", currencyTo='" + super.getToCurrency() + '\'' +
                ", rate=" + rate +
                ", amount=" + super.getAmount() +
                ", date=" + super.getDate() +
                '}';
    }
}
