package ru.giv13.infocrm.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    @JsonProperty("password_confirmation")
    private String passwordConfirmation;
}
