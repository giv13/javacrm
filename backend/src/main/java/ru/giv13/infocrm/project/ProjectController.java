package ru.giv13.infocrm.project;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping()
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).PROJECT_READ)")
    public List<Project> getAll() {
        return projectService.getAll();
    }
}
