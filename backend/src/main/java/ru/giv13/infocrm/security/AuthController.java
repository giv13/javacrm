package ru.giv13.infocrm.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.giv13.infocrm.system.Response;
import ru.giv13.infocrm.user.dto.LoginRequest;
import ru.giv13.infocrm.user.dto.RegisterRequest;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    public ResponseEntity<Response> register(@RequestBody RegisterRequest request) {
        return Response.ok(authService.register(request));
    }

    @PostMapping("login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest request) {
        return Response.ok(authService.login(request));
    }
}
