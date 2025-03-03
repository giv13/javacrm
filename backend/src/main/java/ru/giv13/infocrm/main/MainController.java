package ru.giv13.infocrm.main;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.giv13.infocrm.user.User;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MainController {
    @GetMapping
    public String index(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return "Твои роли и разрешения: " + user.getAuthorities();
    }
}
