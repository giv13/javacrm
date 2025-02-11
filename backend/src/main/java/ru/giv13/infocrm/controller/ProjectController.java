package ru.giv13.infocrm.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.giv13.infocrm.model.EPermisson;

@RestController
@RequestMapping("projects")
public class ProjectController {
    @GetMapping("get")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.model.EPermisson).PROJECT_READ)")
    public String getAll() {
        return "У вас есть разрешение " + EPermisson.PROJECT_READ;
    }

    @GetMapping("post")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.model.EPermisson).PROJECT_CREATE)")
    public String create() {
        return "У вас есть разрешение " + EPermisson.PROJECT_CREATE;
    }

    @GetMapping("put")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.model.EPermisson).PROJECT_UPDATE)")
    public String update() {
        return "У вас есть разрешение " + EPermisson.PROJECT_UPDATE;
    }

    @GetMapping("delete")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.model.EPermisson).PROJECT_DELETE)")
    public String delete() {
        return "У вас есть разрешение " + EPermisson.PROJECT_DELETE;
    }
}
