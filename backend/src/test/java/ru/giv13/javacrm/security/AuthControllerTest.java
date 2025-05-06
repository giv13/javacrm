package ru.giv13.javacrm.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.giv13.javacrm.system.ExceptionHandlerAdvice;
import ru.giv13.javacrm.user.ERole;
import ru.giv13.javacrm.user.Role;
import ru.giv13.javacrm.user.User;
import ru.giv13.javacrm.user.dto.UserLoginDto;
import ru.giv13.javacrm.user.dto.UserProfileDto;
import ru.giv13.javacrm.user.dto.UserRegisterDto;

import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;

    @Spy
    private ModelMapper modelMapper;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    AuthController authController;

    private MockMvc mockMvc;

    private final Integer id = 1;
    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).setControllerAdvice(ExceptionHandlerAdvice.class).build();

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
    void testRegisterSuccess() throws Exception {
        // Given
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setName("Тестовый пользователь");
        userRegisterDto.setUsername("user");
        userRegisterDto.setEmail("test@mail.ru");
        userRegisterDto.setPassword("123$qweR");
        userRegisterDto.setPasswordConfirmation("123$qweR");
        String json = objectMapper.writeValueAsString(userRegisterDto);

        UserProfileDto userProfileDto = modelMapper.map(user, UserProfileDto.class);

        given(authService.register(userRegisterDto)).willReturn(userProfileDto);

        // When and then
        mockMvc.perform(post("/auth/register").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.name").value(userRegisterDto.getName()))
                .andExpect(jsonPath("$.data.username").value(userRegisterDto.getUsername()))
                .andExpect(jsonPath("$.data.email").value(userRegisterDto.getEmail()))
                .andExpect(jsonPath("$.data.authorities", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data.authorities[0]").value(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().getFirst()));
    }

    @Test
    void testRegisterValidationError() throws Exception {
        // Given
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        String json = objectMapper.writeValueAsString(userRegisterDto);

        // When and then
        mockMvc.perform(post("/auth/register").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
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
    void testLoginSuccess() throws Exception {
        // Given
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername("user");
        userLoginDto.setPassword("user");
        String json = objectMapper.writeValueAsString(userLoginDto);

        UserProfileDto userProfileDto = modelMapper.map(user, UserProfileDto.class);

        given(authService.login(userLoginDto)).willReturn(userProfileDto);

        // When and then
        mockMvc.perform(post("/auth/login").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.username").value(userLoginDto.getUsername()))
                .andExpect(jsonPath("$.data.authorities", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data.authorities[0]").value(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().getFirst()));
    }

    @Test
    void testLoginValidationError() throws Exception {
        // Given
        UserLoginDto userLoginDto = new UserLoginDto();
        String json = objectMapper.writeValueAsString(userLoginDto);

        // When and then
        mockMvc.perform(post("/auth/login").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error.username").isNotEmpty())
                .andExpect(jsonPath("$.error.password").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}