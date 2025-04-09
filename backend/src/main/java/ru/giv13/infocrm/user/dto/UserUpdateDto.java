package ru.giv13.infocrm.user.dto;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.giv13.infocrm.security.Password;
import ru.giv13.infocrm.security.PasswordConfirmation;
import ru.giv13.infocrm.system.NullOrNotBlank;
import ru.giv13.infocrm.user.Role;

import java.util.List;

@Data
@GroupSequence({ UserUpdateDto.class, UserUpdateDto.FirstGroup.class, UserUpdateDto.SecondGroup.class })
@PasswordConfirmation(groups = UserUpdateDto.SecondGroup.class)
public class UserUpdateDto implements PasswordConfirmable {
    public interface FirstGroup {}
    public interface SecondGroup {}

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

    private byte[] avatar;

    private boolean isActive;

    private List<Role> roles;
}
