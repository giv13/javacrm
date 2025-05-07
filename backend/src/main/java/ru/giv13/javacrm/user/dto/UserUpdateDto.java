package ru.giv13.javacrm.user.dto;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.giv13.javacrm.security.Password;
import ru.giv13.javacrm.security.PasswordConfirmation;
import ru.giv13.javacrm.system.Exists;
import ru.giv13.javacrm.system.NullOrNotBlank;

import java.util.Set;

@Data
@GroupSequence({ UserUpdateDto.class, UserUpdateDto.FirstGroup.class, UserUpdateDto.SecondGroup.class, UserUpdateDto.ThirdGroup.class })
@PasswordConfirmation(groups = UserUpdateDto.SecondGroup.class)
@Exists(groups = UserUpdateDto.ThirdGroup.class)
public class UserUpdateDto implements PasswordConfirmable, ExistsCheckable {
    public interface FirstGroup {}
    public interface SecondGroup {}
    public interface ThirdGroup {}

    @NullOrNotBlank(groups = FirstGroup.class)
    private String name;

    @NullOrNotBlank(groups = FirstGroup.class)
    @Pattern(regexp = "^\\w+$", message = "Должно содержать только латинские буквы, цифры и символ _", groups = SecondGroup.class)
    private String username;

    @NullOrNotBlank(groups = FirstGroup.class)
    @Email(groups = SecondGroup.class)
    private String email;

    @NullOrNotBlank(groups = FirstGroup.class)
    @Password(groups = SecondGroup.class)
    private String password;

    @NullOrNotBlank(groups = FirstGroup.class)
    private String passwordConfirmation;

    private String notes;

    private Boolean active;

    private Set<Integer> roles;
}
