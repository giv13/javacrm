package ru.giv13.infocrm.security;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.giv13.infocrm.user.LoginRequest;
import ru.giv13.infocrm.user.RegisterRequest;
import ru.giv13.infocrm.user.User;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    @JsonView(User.Profile.class)
    public User register(@Validated(RegisterRequest.OrderedValidationGroups.class) @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("login")
    @JsonView(User.Profile.class)
    public User login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("logout")
    public void logout() {
        authService.logout();
    }
}
