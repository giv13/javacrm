package ru.giv13.infocrm.user;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @JsonView(UserDto.Detail.class)
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).USER_READ)")
    public List<UserDto> getAll() {
        return userService.getAll();
    }
}
