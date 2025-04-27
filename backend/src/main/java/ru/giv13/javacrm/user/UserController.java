package ru.giv13.javacrm.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.giv13.javacrm.user.dto.UserCreateDto;
import ru.giv13.javacrm.user.dto.UserDto;
import ru.giv13.javacrm.user.dto.UserUpdateDto;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "Пользователи")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).USER_READ)")
    @Operation(summary = "Получить список пользователей", description = "Необходимые права: USER_READ")
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).USER_CREATE)")
    @Operation(summary = "Создать пользователя", description = "Необходимые права: USER_CREATE")
    public UserDto create(@Valid @RequestBody UserCreateDto userCreateDto) {
        return userService.create(userCreateDto);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).USER_UPDATE)")
    @Operation(summary = "Обновить пользователя", description = "Необходимые права: USER_UPDATE")
    public UserDto update(
            @PathVariable("id") @Parameter(description = "Идентификатор пользователя") Integer id,
            @Valid @RequestBody UserUpdateDto userUpdateDto
    ) {
        return userService.update(id, userUpdateDto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority(T(ru.giv13.javacrm.user.EPermisson).USER_DELETE)")
    @Operation(summary = "Удалить пользователя", description = "Необходимые права: USER_DELETE")
    public void delete(@PathVariable("id") @Parameter(description = "Идентификатор пользователя") Integer id) {
        userService.delete(id);
    }

    @PatchMapping(value = "{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAnyAuthority(T(ru.giv13.javacrm.user.EPermisson).USER_CREATE, T(ru.giv13.javacrm.user.EPermisson).USER_UPDATE)")
    @Operation(summary = "Загрузить аватар для пользователя", description = "Необходимые права: USER_CREATE или USER_UPDATE")
    public UserDto uploadAvatar(
            @PathVariable("id") @Parameter(description = "Идентификатор пользователя") Integer id,
            @RequestParam(value = "avatar", required = false) @Parameter(description = "Файл изображения, доступные форматы: jpg, png. Для удаления аватара передайте пустое значение", required = true, allowEmptyValue = true) MultipartFile avatar
    ) {
        return userService.uploadAvatar(id, avatar);
    }
}
