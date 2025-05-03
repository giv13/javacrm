package ru.giv13.javacrm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.giv13.javacrm.user.*;
import ru.giv13.javacrm.user.dto.UserLoginDto;
import ru.giv13.javacrm.user.dto.UserProfileDto;
import ru.giv13.javacrm.user.dto.UserRegisterDto;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private final Integer id = 1;
    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
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
    void testRegisterSuccess() {
        // Given
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setName("Тестовый пользователь");
        userRegisterDto.setUsername("user");
        userRegisterDto.setEmail("test@mail.ru");

        given(roleRepository.findByName(ERole.USER)).willReturn(Optional.of(role));
        given(userRepository.save(any(User.class))).willReturn(user);

        // When
        UserProfileDto userProfileDto = authService.register(userRegisterDto);

        // Then
        assertEquals(userProfileDto.getName(), userRegisterDto.getName());
        assertEquals(userProfileDto.getUsername(), userRegisterDto.getUsername());
        assertEquals(userProfileDto.getEmail(), userRegisterDto.getEmail());
        assertEquals(userProfileDto.getAuthorities(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        then(roleRepository).should().findByName(ERole.USER);
        then(userRepository).should().save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        // Given
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername("user");
        userLoginDto.setPassword("user");

        given(authenticationManager.authenticate(any())).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(user);
        given(userRepository.save(any(User.class))).willReturn(user);

        // When
        UserProfileDto userProfileDto = authService.login(userLoginDto);

        // Then
        assertEquals(userProfileDto.getUsername(), userLoginDto.getUsername());
        assertEquals(userProfileDto.getAuthorities(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        then(authenticationManager).should().authenticate(any());
        then(authentication).should().getPrincipal();
        then(userRepository).should().save(any(User.class));
    }
}