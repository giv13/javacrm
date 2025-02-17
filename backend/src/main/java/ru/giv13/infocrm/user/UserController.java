package ru.giv13.infocrm.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.giv13.infocrm.system.Response;

@RestController
@RequestMapping("users")
public class UserController {
    @GetMapping("read")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).USER_READ)")
    public ResponseEntity<Response> getAll() {
        return Response.ok("У вас есть разрешение " + EPermisson.USER_READ);
    }

    @GetMapping("create")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).USER_CREATE)")
    public ResponseEntity<Response> create() {
        return Response.ok("У вас есть разрешение " + EPermisson.USER_CREATE);
    }

    @GetMapping("update")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).USER_UPDATE)")
    public ResponseEntity<Response> update() {
        return Response.ok("У вас есть разрешение " + EPermisson.USER_UPDATE);
    }

    @GetMapping("delete")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).USER_DELETE)")
    public ResponseEntity<Response> delete() {
        return Response.ok("У вас есть разрешение " + EPermisson.USER_DELETE);
    }
}
