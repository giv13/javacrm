package ru.giv13.javacrm.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCreateDto extends UserRegisterDto {
    private String notes;
    private boolean isActive = true;
    private Set<Integer> roles;
}
