package ru.giv13.javacrm.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.giv13.javacrm.system.ExceptionHandlerAdvice;
import ru.giv13.javacrm.user.dto.UserCreateDto;
import ru.giv13.javacrm.user.dto.UserDto;
import ru.giv13.javacrm.user.dto.UserUpdateDto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @Spy
    private ModelMapper modelMapper;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    UserController userController;

    private MockMvc mockMvc;

    private final Integer id = 1;
    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(ExceptionHandlerAdvice.class).build();

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setCollectionsMergeEnabled(false);

        role = new Role()
                .setId(id)
                .setName(ERole.USER)
                .setDisplayName("Пользователь");

        user = new User()
                .setId(id)
                .setName("Тестовый пользователь")
                .setUsername("user")
                .setEmail("test@mail.ru")
                .setRoles(Set.of(role));
    }

    @Test
    void testGetAllSuccess() throws Exception {
        // Given
        User copiedUser = new User();
        modelMapper.map(user, copiedUser);
        copiedUser
                .setId(2)
                .setName("Тестовый пользователь 2")
                .setUsername("user2")
                .setEmail("test2@mail.ru");
        List<User> users = List.of(user, copiedUser);
        List<UserDto> usersDto = users.stream().map(user -> modelMapper.map(user, UserDto.class)).toList();

        given(userService.getAll()).willReturn(usersDto);

        // When and then
        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data", Matchers.hasSize(users.size())));
    }

    @Test
    void testCreateSuccess() throws Exception {
        // Given
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Тестовый пользователь");
        userCreateDto.setUsername("user");
        userCreateDto.setEmail("test@mail.ru");
        userCreateDto.setRoles(Set.of(id));
        userCreateDto.setPassword("123$qweR");
        userCreateDto.setPasswordConfirmation("123$qweR");
        String json = objectMapper.writeValueAsString(userCreateDto);

        UserDto createdUserDto = modelMapper.map(user, UserDto.class);

        given(userService.create(userCreateDto)).willReturn(createdUserDto);

        // When and then
        mockMvc.perform(post("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.name").value("Тестовый пользователь"))
                .andExpect(jsonPath("$.data.username").value("user"))
                .andExpect(jsonPath("$.data.email").value("test@mail.ru"))
                .andExpect(jsonPath("$.data.roles[0].id").value(id));
    }

    @Test
    void testCreateValidationError() throws Exception {
        // Given
        UserCreateDto userCreateDto = new UserCreateDto();
        String json = objectMapper.writeValueAsString(userCreateDto);

        // When and then
        mockMvc.perform(post("/users").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error.name").isNotEmpty())
                .andExpect(jsonPath("$.error.username").isNotEmpty())
                .andExpect(jsonPath("$.error.email").isNotEmpty())
                .andExpect(jsonPath("$.error.password").isNotEmpty())
                .andExpect(jsonPath("$.error.passwordConfirmation").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }


    @Test
    void testUpdateSuccess() throws Exception {
        // Given
        Integer updatedId = 2;

        User updatedUser = new User();
        modelMapper.map(user, updatedUser);
        updatedUser
                .setName("Обновленный пользователь")
                .setEmail("updated@mail.ru");
        UserDto updatedUserDto = modelMapper.map(updatedUser, UserDto.class);

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Обновленный пользователь");
        userUpdateDto.setEmail("updated@mail.ru");
        String json = objectMapper.writeValueAsString(userUpdateDto);

        given(userService.update(id, userUpdateDto)).willReturn(updatedUserDto);

        // When and then
        mockMvc.perform(put("/users/" + id).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.name").value("Обновленный пользователь"))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()))
                .andExpect(jsonPath("$.data.email").value("updated@mail.ru"))
                .andExpect(jsonPath("$.data.active").value(user.isActive()))
                .andExpect(jsonPath("$.data.roles[0].id").value(user.getRoles().stream().findFirst().map(Role::getId).orElse(null)));
    }

    @Test
    void testUpdateValidationError() throws Exception {
        // Given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("@");
        userUpdateDto.setPassword("12345");
        userUpdateDto.setPasswordConfirmation("qwerty");
        String json = objectMapper.writeValueAsString(userUpdateDto);

        // When and then
        mockMvc.perform(put("/users/" + id).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error.username").isNotEmpty())
                .andExpect(jsonPath("$.error.password").isNotEmpty())
                .andExpect(jsonPath("$.error.passwordConfirmation").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUpdateNotFoundError() throws Exception {
        // Given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        String json = objectMapper.writeValueAsString(userUpdateDto);

        doThrow(new ObjectNotFoundException(id, "user")).when(userService).update(id, userUpdateDto);

        // When and then
        mockMvc.perform(put("/users/" + id).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteSuccess() throws Exception {
        // Given
        doNothing().when(userService).delete(id);

        // When and then
        mockMvc.perform(delete("/users/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteNotFoundError() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException(id, "user")).when(userService).delete(id);

        // When and then
        mockMvc.perform(delete("/users/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUploadAvatarSuccess() throws Exception {
        // Given
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        MockMultipartFile avatar = new MockMultipartFile("avatar", "test.png", MediaType.IMAGE_PNG_VALUE, baos.toByteArray());

        User updatedUser = new User();
        modelMapper.map(user, updatedUser);
        updatedUser.setAvatar(avatar.getBytes());
        UserDto updatedUserDto = modelMapper.map(updatedUser, UserDto.class);

        given(userService.uploadAvatar(id, avatar)).willReturn(updatedUserDto);

        // When and then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/users/" + id).file(avatar))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.avatar").value(Base64.getEncoder().encodeToString(avatar.getBytes())));
    }

    @Test
    void testUploadAvatarNotFoundError() throws Exception {
        // Given
        MockMultipartFile avatar = new MockMultipartFile("avatar", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

        doThrow(new ObjectNotFoundException(id, "user")).when(userService).uploadAvatar(id, avatar);

        // When and then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/users/" + id).file(avatar))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}