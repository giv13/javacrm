package ru.giv13.javacrm.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@Tag(name = "Роли")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).USER_READ)")
    @Operation(summary = "Получить список ролей", description = "Необходимые права: USER_READ")
    public List<Role> getAll() {
        return roleService.getAll();
    }
}
