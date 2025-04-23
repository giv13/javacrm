package ru.giv13.javacrm.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.giv13.javacrm.user.dto.UserLoginDto;
import ru.giv13.javacrm.user.dto.UserProfileDto;
import ru.giv13.javacrm.user.dto.UserRegisterDto;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    public UserProfileDto register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        return authService.register(userRegisterDto);
    }

    @PostMapping("login")
    public UserProfileDto login(@Valid @RequestBody UserLoginDto userLoginDto) {
        return authService.login(userLoginDto);
    }

    @PostMapping("refresh")
    public void refresh(HttpServletRequest request) {
        authService.refresh(request);
    }

    @PostMapping("logout")
    public void logout() {
        authService.logout();
    }
}
