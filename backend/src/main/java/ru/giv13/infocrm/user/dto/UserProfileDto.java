package ru.giv13.infocrm.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileDto {
    private String name;
    private String username;
    private String email;
    private byte[] avatar;
    private List<String> authorities;
}
