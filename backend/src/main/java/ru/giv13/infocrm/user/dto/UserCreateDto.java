package ru.giv13.infocrm.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.giv13.infocrm.user.Role;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCreateDto extends UserRegisterDto {
    private String notes;
    private byte[] avatar;
    private boolean isActive;
    private List<Role> roles;
}
