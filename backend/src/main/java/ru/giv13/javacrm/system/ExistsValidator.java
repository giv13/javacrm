package ru.giv13.javacrm.system;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import ru.giv13.javacrm.user.UserRepository;
import ru.giv13.javacrm.user.dto.ExistsCheckable;

import java.util.Map;

@RequiredArgsConstructor
public class ExistsValidator implements ConstraintValidator<Exists, ExistsCheckable> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(ExistsCheckable existsCheckable, ConstraintValidatorContext context) {
        boolean isExists = false;
        int id = 0;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Map<?, ?> uriAttributes = (Map<?, ?>) attributes.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (uriAttributes.containsKey("id")) {
                id = Integer.parseInt((String) uriAttributes.get("id"));
            }
        }

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
