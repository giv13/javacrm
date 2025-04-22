package ru.giv13.javacrm.project.dto;

import lombok.Data;
import ru.giv13.javacrm.project.Status;
import ru.giv13.javacrm.user.dto.UserIdDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectDto {
    private Integer id;
    private String name;
    private String description;
    private Status status;
    private UserIdDto responsible;
    private List<UserIdDto> participants;
    private LocalDateTime createdAt;

    public Integer getResponsible() {
        if (responsible == null) return null;
        return responsible.getId();
    }

    public List<Integer> getParticipants() {
        if (participants == null) return new ArrayList<>();
        return participants.stream().map(UserIdDto::getId).toList();
    }
}
