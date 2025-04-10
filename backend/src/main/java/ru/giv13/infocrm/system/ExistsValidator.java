package ru.giv13.infocrm.system;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ru.giv13.infocrm.user.UserRepository;
import ru.giv13.infocrm.user.dto.ExistsCheckable;

import java.util.Optional;

@RequiredArgsConstructor
public class ExistsValidator implements ConstraintValidator<Exists, ExistsCheckable> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(ExistsCheckable existsCheckable, ConstraintValidatorContext context) {
        boolean isExists = false;
        int id = Optional.ofNullable(existsCheckable.getId()).orElse(0);
        if (existsCheckable.getUsername() != null && userRepository.existsByUsernameAndIdNot(existsCheckable.getUsername(), id)) {
            context.buildConstraintViolationWithTemplate("Имя пользователя уже занято")
                    .addPropertyNode("username")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            isExists = true;
        }
        if (existsCheckable.getEmail() != null && userRepository.existsByEmailAndIdNot(existsCheckable.getEmail(), id)) {
            context.buildConstraintViolationWithTemplate("E-mail уже занят")
                    .addPropertyNode("email")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            isExists = true;
        }
        return !isExists;
    }
}
