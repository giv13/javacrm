package ru.giv13.javacrm.project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.giv13.javacrm.system.NullOrNotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectUpdateDto extends ProjectRequestDto {
    @NullOrNotBlank
    private String name;
}
