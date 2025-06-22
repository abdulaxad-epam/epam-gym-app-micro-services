package epam.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class FutureValidator implements ConstraintValidator<Future, LocalDate> {
    private Integer toYear;

    @Override
    public void initialize(Future constraintAnnotation) {
        this.toYear = constraintAnnotation.toYear();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalDate now = LocalDate.now();

        return value.getYear() <= toYear && !value.isBefore(now);
    }
}

