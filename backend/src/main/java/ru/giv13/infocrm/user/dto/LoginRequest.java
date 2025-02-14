package ru.giv13.infocrm.user.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
