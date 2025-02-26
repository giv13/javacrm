package ru.giv13.infocrm.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ru.giv13.infocrm.security.Password;
import ru.giv13.infocrm.security.PasswordConfirmation;

@PasswordConfirmation(groups = RegisterRequest.SecondValidationGroup.class)
public record RegisterRequest(
        @NotBlank(groups = FirstValidationGroup.class) String username,
        @NotBlank(groups = FirstValidationGroup.class) @Email(groups = SecondValidationGroup.class) String email,
        @NotBlank(groups = FirstValidationGroup.class) @Password(groups = SecondValidationGroup.class) String password,
        @JsonProperty("password_confirmation") @NotBlank(groups = FirstValidationGroup.class) String passwordConfirmation
) {
    public interface FirstValidationGroup {}
    public interface SecondValidationGroup {}
    @GroupSequence({ FirstValidationGroup.class, SecondValidationGroup.class })
    public interface OrderedValidationGroups {}
}
