package ru.giv13.infocrm.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class ProjectRequestDto {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Integer status;

    @NotNull
    private Integer responsible;

    private Set<Integer> participants;
}
