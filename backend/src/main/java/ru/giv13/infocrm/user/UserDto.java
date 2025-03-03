package ru.giv13.infocrm.user;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    public interface Profile {}
    public interface Detail {}

    @JsonView(Detail.class)
    private Integer id;
    private String name;
    private String username;
    private String email;
    @JsonView(Detail.class)
    private String notes;
    private byte[] avatar;
    @JsonView(Detail.class)
    private boolean active;
    @JsonView(Profile.class)
    private Set<String> authorities;
    @JsonView(Detail.class)
    private Set<RoleDto> roles;
}
