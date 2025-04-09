package ru.giv13.infocrm.system;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Exists {
    String message() default "Already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
