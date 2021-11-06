package com.scalable.capital.exchange.rate.model;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scalable.capital.exchange.rate.serializer.ExchangeStringDeserializer;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonPropertyOrder({ "totalCurrencies", "currencyPairCountMap",
        "currencyCountMap" })
public class Statistics {

    @JsonDeserialize(keyUsing = ExchangeStringDeserializer.class)
    private Map<Pair<String, String>, Integer> currencyPairCountMap = new HashMap<>();
    private Map<String, Integer> currencyCountMap = new HashMap<>();

    private int totalCurrencies;

    public Map<Pair<String, String>, Integer> getCurrencyPairCountMap() {
        return currencyPairCountMap;
    }

    public Map<String, Integer> getCurrencyCountMap() {
        return currencyCountMap;
    }

    public void setCurrencyPairCountMap(Map<Pair<String, String>, Integer> currencyPairCountMap) {
        this.currencyPairCountMap = currencyPairCountMap;
    }

    public void setCurrencyCountMap(Map<String, Integer> currencyCountMap) {
        this.currencyCountMap = currencyCountMap;
    }

    public int getTotalCurrencies() {
        return totalCurrencies;
    }

    public void setTotalCurrencies(int totalCurrencies) {
        this.totalCurrencies = totalCurrencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return totalCurrencies == that.totalCurrencies && Objects.equals(currencyPairCountMap, that.currencyPairCountMap)
                && Objects.equals(currencyCountMap, that.currencyCountMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCountMap, currencyPairCountMap, totalCurrencies);
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "currencyPairCountMap=" + currencyPairCountMap +
                ", currencyCountMap=" + currencyCountMap +
                ", totalCurrencies=" + totalCurrencies +
                '}';
    }


}
