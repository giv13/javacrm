package ru.giv13.infocrm.project;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.giv13.infocrm.project.dto.ProjectDto;
import ru.giv13.infocrm.project.dto.ProjectRequestDto;

import java.util.List;

@RestController
@RequestMapping("projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping()
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).PROJECT_READ)")
    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).PROJECT_CREATE)")
    public ProjectDto create(@Valid @RequestBody ProjectRequestDto projectRequestDto) {
        return projectService.create(projectRequestDto);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).PROJECT_UPDATE)")
    public ProjectDto update(@PathVariable("id") Integer id, @Valid @RequestBody ProjectRequestDto projectRequestDto) {
        return projectService.update(id, projectRequestDto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).PROJECT_DELETE)")
    public void delete(@PathVariable("id") Integer id) {
        projectService.delete(id);
    }
}
