package ru.giv13.infocrm.user.dto;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.giv13.infocrm.security.Password;
import ru.giv13.infocrm.security.PasswordConfirmation;

@Data
@GroupSequence({ UserRegisterDto.class, UserRegisterDto.FirstGroup.class, UserRegisterDto.SecondGroup.class })
@PasswordConfirmation(groups = UserRegisterDto.SecondGroup.class)
public class UserRegisterDto implements PasswordConfirmable {
    public interface FirstGroup {}
    public interface SecondGroup {}

    @NotBlank(groups = FirstGroup.class)
    private String name;

    @NotBlank(groups = FirstGroup.class)
    @Pattern(regexp = "^\\w+$", message = "Должно содержать только латинские буквы, цифры и символ _", groups = SecondGroup.class)
    private String username;

    @NotBlank(groups = FirstGroup.class)
    @Email(groups = SecondGroup.class)
    private String email;

    @NotBlank(groups = FirstGroup.class)
    @Password(groups = SecondGroup.class)
    private String password;

    @NotBlank(groups = FirstGroup.class)
    private String passwordConfirmation;
}
