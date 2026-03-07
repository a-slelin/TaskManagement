package a.slelin.work.task.management.util.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Phone {

    String message() default "{a.slelin.work.validation.phone.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
