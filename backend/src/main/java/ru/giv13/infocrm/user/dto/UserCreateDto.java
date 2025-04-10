package ru.giv13.infocrm.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCreateDto extends UserRegisterDto {
    private String notes;
    private byte[] avatar;
    private boolean isActive;
    private Set<Integer> roles;
}
