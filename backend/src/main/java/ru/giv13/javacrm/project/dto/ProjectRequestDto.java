package ru.giv13.javacrm.project.dto;

import lombok.Data;

import java.util.Set;

@Data
public abstract class ProjectRequestDto {
    private String name;
    private String description;
    private Integer status = null;
    private Integer responsible = null;
    private Set<Integer> participants;
}
