package ru.giv13.infocrm.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(String username, String email, String password, @JsonProperty("password_confirmation") String passwordConfirmation) {
}
