package com.lar.customeronboarding.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinimumAgeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinimumAge {

    String message() default "customer must be at least {value} years old";

    int value() default 18;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
