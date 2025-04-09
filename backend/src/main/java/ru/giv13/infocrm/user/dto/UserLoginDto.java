package ru.giv13.infocrm.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
