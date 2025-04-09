package ru.giv13.infocrm.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.giv13.infocrm.user.dto.UserCreateDto;
import ru.giv13.infocrm.user.dto.UserDto;
import ru.giv13.infocrm.user.dto.UserUpdateDto;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).USER_READ)")
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).USER_CREATE)")
    public UserDto create(@Valid @RequestBody UserCreateDto userCreateDto) {
        return userService.create(userCreateDto);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority(T(ru.giv13.infocrm.user.EPermisson).USER_UPDATE)")
    public UserDto update(@PathVariable("id") Integer id, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userService.update(id, userUpdateDto);
    }
}
