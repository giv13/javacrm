package ru.giv13.infocrm.security;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.giv13.infocrm.user.dto.RegisterRequest;

public class PasswordConfirmationValidator implements ConstraintValidator<PasswordConfirmation, RegisterRequest> {
    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request.password().equals(request.passwordConfirmation())) {
            return true;
        }
        context.buildConstraintViolationWithTemplate("Пароли не совпадают")
                .addPropertyNode("passwordConfirmation")
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
