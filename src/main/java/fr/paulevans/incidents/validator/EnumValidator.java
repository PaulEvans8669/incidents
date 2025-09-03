package fr.paulevans.incidents.validator;

import fr.paulevans.incidents.annotation.ValidEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private String allowedValues;

    @Override
    public void initialize(ValidEnum annotation) {
        Class<? extends Enum<?>> enumClass = annotation.enumClass();
        allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotNull should be used separately if needed

        boolean valid = allowedValues.contains(value);

        if (!valid) {
            // Disable the default error message
            context.disableDefaultConstraintViolation();
            // Build a custom one including the actual values
            context.buildConstraintViolationWithTemplate(
                    "must be one of the allowed values: " + allowedValues
            ).addConstraintViolation();
        }

        return valid;
    }
}
