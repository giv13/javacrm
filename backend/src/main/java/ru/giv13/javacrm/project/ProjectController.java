package ru.giv13.javacrm.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.giv13.javacrm.project.dto.ProjectDto;
import ru.giv13.javacrm.project.dto.ProjectCreateDto;
import ru.giv13.javacrm.project.dto.ProjectUpdateDto;

import java.util.List;

@RestController
@RequestMapping("projects")
@RequiredArgsConstructor
@Tag(name = "Проекты")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping()
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).PROJECT_READ)")
    @Operation(summary = "Получить список проектов", description = "Необходимые права: PROJECT_READ")
    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).PROJECT_CREATE)")
    @Operation(summary = "Создать проект", description = "Необходимые права: PROJECT_CREATE")
    public ProjectDto create(@Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectService.create(projectCreateDto);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).PROJECT_UPDATE)")
    @Operation(summary = "Обновить проект", description = "Необходимые права: PROJECT_UPDATE")
    public ProjectDto update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody @Parameter(description = "Идентификатор проекта") ProjectUpdateDto projectUpdateDto
    ) {
        return projectService.update(id, projectUpdateDto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).PROJECT_DELETE)")
    @Operation(summary = "Удалить проект", description = "Необходимые права: PROJECT_DELETE")
    public void delete(@PathVariable("id") @Parameter(description = "Идентификатор проекта") Integer id) {
        projectService.delete(id);
    }
}
