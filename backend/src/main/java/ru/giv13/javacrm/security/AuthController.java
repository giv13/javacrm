package ru.giv13.javacrm.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    @Operation(summary = "Регистрация в системе")
    public UserProfileDto register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        return authService.register(userRegisterDto);
    }

    @PostMapping("login")
    @Operation(summary = "Вход в систему")
    public UserProfileDto login(@Valid @RequestBody UserLoginDto userLoginDto) {
        return authService.login(userLoginDto);
    }

    @PostMapping("refresh")
    @Operation(summary = "Получение нового JWT по Refresh-токену")
    public void refresh(HttpServletRequest request) {
        authService.refresh(request);
    }

    @PostMapping("logout")
    @Operation(summary = "Выход из системы")
    public void logout() {
        authService.logout();
    }
}
