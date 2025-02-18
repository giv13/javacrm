package ru.giv13.infocrm.user.dto;

import java.util.Set;

public record UserDto(String name, String username, String email, byte[] image, Set<String> authorities) {
}
