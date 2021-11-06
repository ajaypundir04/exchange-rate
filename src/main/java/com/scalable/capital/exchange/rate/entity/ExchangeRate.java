package com.scalable.capital.exchange.rate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;


public class ExchangeRate implements Serializable {

    private static final long serialVersionUID = 2432078397208040228L;
    private String currency;
    private Double rate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    public ExchangeRate(String currency, Double rate, LocalDate date) {
        this.currency = currency;
        this.rate = rate;
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ExchangeRate [ currency=" + currency + ", rate=" + rate + ", date=" + date + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(currency, that.currency) && Objects.equals(rate, that.rate) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, rate, date);
    }
}
