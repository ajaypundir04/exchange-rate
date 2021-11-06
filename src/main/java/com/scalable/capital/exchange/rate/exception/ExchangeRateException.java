package com.scalable.capital.exchange.rate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ExchangeRateException extends RuntimeException {
    public ExchangeRateException(String message) {
        super(message);
    }

}
