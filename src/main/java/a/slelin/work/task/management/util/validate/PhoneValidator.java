package a.slelin.work.task.management.util.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext cvc) {
        return s == null || s.matches("^[+]?[(]?[0-9]{1,4}[)]?[- ./0-9]{5,15}$");
    }
}
