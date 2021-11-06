package com.scalable.capital.exchange.rate.exception;

public class EcbResponseException extends RuntimeException {

    public EcbResponseException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
