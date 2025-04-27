package ru.giv13.javacrm.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("statuses")
@RequiredArgsConstructor
@Tag(name = "Статусы")
public class StatusController {
    private final StatusService statusService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).PROJECT_READ)")
    @Operation(summary = "Получить список статусов", description = "Необходимые права: PROJECT_READ")
    public List<Status> getAll() {
        return statusService.getAll();
    }
}
