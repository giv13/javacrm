package ru.giv13.infocrm.user.dto;

import lombok.Data;
import ru.giv13.infocrm.project.dto.ProjectIdDto;
import ru.giv13.infocrm.user.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class UserDto {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private String notes;
    private byte[] avatar;
    private boolean isActive;
    private Set<Role> roles;
    private List<ProjectIdDto> projects;

    public List<Integer> getProjects() {
        if (projects == null) return new ArrayList<>();
        return projects.stream().map(ProjectIdDto::getId).toList();
    }
}
