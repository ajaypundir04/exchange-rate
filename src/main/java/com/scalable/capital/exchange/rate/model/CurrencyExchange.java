package com.scalable.capital.exchange.rate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.scalable.capital.exchange.rate.serializer.AmountSerializer;
import com.scalable.capital.exchange.rate.serializer.ExchangeStringDeserializer;
import com.scalable.capital.exchange.rate.util.ExchangeRateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CurrencyExchange {

    private String toCurrency = "EUR";

    @DateTimeFormat(pattern = ExchangeRateUtil.DATE_ATTRIBUTE_FORMAT)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date = LocalDate.now();

    @JsonSerialize(using = AmountSerializer.class)
    @JsonDeserialize(keyUsing = ExchangeStringDeserializer.class)
    private BigDecimal amount;

    public CurrencyExchange(String toCurrency, LocalDate date, BigDecimal amount) {
        this.toCurrency = toCurrency;
        this.date = date;
        this.amount = amount;
    }

    public CurrencyExchange() {

    }


    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Exchange{" +
                "toCurrency='" + toCurrency + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                '}';
    }


}
