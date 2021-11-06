package com.scalable.capital.exchange.rate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CurrencyExchangeRequestValidator.class})
@Documented
public @interface ValidCurrencyExchangeRequest {

    String message() default "Invalid Currency Exchange Request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}