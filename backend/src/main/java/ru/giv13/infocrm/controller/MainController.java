package ru.giv13.infocrm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.giv13.infocrm.model.EPermisson;
import ru.giv13.infocrm.model.ERole;
import ru.giv13.infocrm.model.Permission;
import ru.giv13.infocrm.model.Role;
import ru.giv13.infocrm.repository.PermissionRepository;
import ru.giv13.infocrm.repository.RoleRepository;

import java.util.Set;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MainController {
    private final PermissionRepository permissions;
    private final RoleRepository roles;
    @GetMapping
    public String index() {
        Permission permission = new Permission();
        permission.setName(EPermisson.USER_READ);
        permission.setDisplayName("Пользователи:чтение");
        permissions.save(permission);

        Role role = new Role();
        role.setName(ERole.USER);
        role.setDisplayName("Пользователь");
        role.setPermissions(Set.of(permission));
        roles.save(role);

        return "It works!";
    }
}
