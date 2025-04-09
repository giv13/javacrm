package ru.giv13.infocrm.security;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.giv13.infocrm.user.dto.PasswordConfirmable;

public class PasswordConfirmationValidator implements ConstraintValidator<PasswordConfirmation, PasswordConfirmable> {
    @Override
    public boolean isValid(PasswordConfirmable passwordConfirmable, ConstraintValidatorContext context) {
        if (passwordConfirmable.getPassword() == null || passwordConfirmable.getPassword().equals(passwordConfirmable.getPasswordConfirmation())) {
            return true;
        }
        context.buildConstraintViolationWithTemplate("Пароли не совпадают")
                .addPropertyNode("passwordConfirmation")
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
