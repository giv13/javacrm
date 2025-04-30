package ru.giv13.javacrm.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectCreateDto extends ProjectRequestDto {
    @NotBlank
    private String name;

    @NotNull
    private Integer status;

    @NotNull
    private Integer responsible;
}
