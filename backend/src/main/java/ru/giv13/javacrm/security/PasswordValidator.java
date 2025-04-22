package ru.giv13.javacrm.security;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Properties;
import java.util.ResourceBundle;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) return true;
        org.passay.PasswordValidator validator = new org.passay.PasswordValidator(
                getMessageResolver(),
                new LengthRule(8, 30),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule()
        );
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.buildConstraintViolationWithTemplate(String.join("|", validator.getMessages(result)))
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }

    private static MessageResolver getMessageResolver() {
        ResourceBundle bundle = ResourceBundle.getBundle("passay", LocaleContextHolder.getLocale());
        Properties properties = new Properties();
        bundle.keySet().forEach(key -> properties.setProperty(key, bundle.getString(key)));
        return new PropertiesMessageResolver(properties);
    }
}
