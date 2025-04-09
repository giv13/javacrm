package ru.giv13.infocrm.user.dto;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.giv13.infocrm.security.Password;
import ru.giv13.infocrm.security.PasswordConfirmation;
import ru.giv13.infocrm.system.Exists;

@Data
@GroupSequence({ UserRegisterDto.class, UserRegisterDto.FirstGroup.class, UserRegisterDto.SecondGroup.class, UserRegisterDto.ThirdGroup.class })
@PasswordConfirmation(groups = UserRegisterDto.SecondGroup.class)
@Exists(groups = UserRegisterDto.ThirdGroup.class)
public class UserRegisterDto implements PasswordConfirmable, ExistsCheckable {
    public interface FirstGroup {}
    public interface SecondGroup {}
    public interface ThirdGroup {}

    @Null
    private Integer id;

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
