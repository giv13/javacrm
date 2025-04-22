package ru.giv13.javacrm.user.dto;

import lombok.Data;
import ru.giv13.javacrm.project.dto.ProjectIdDto;
import ru.giv13.javacrm.user.Role;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private String notes;
    private byte[] avatar;
    private boolean isActive;
    private List<Role> roles;
    private List<ProjectIdDto> projects;

    public List<Integer> getProjects() {
        if (projects == null) return new ArrayList<>();
        return projects.stream().map(ProjectIdDto::getId).toList();
    }
}
