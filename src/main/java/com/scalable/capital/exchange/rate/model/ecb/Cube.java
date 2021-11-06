package com.scalable.capital.exchange.rate.model.ecb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cube {

    @JacksonXmlProperty( localName = "currency", isAttribute = true)
    private String currency;
    @JacksonXmlProperty( localName = "rate", isAttribute = true)
    private Double rate;

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

}
