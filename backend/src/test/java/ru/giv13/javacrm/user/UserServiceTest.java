package ru.giv13.javacrm.user;

import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import ru.giv13.javacrm.user.dto.UserCreateDto;
import ru.giv13.javacrm.user.dto.UserDto;
import ru.giv13.javacrm.user.dto.UserUpdateDto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private final Integer id = 1;
    private Role role;
    private User user;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);

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
    void testGetAllSuccess() {
        // Given
        User copiedUser = new User();
        modelMapper.map(user, copiedUser);
        copiedUser
                .setId(2)
                .setName("Тестовый пользователь 2")
                .setUsername("user2")
                .setEmail("test2@mail.ru");
        List<User> users = List.of(user, copiedUser);

        given(userRepository.findAll()).willReturn(users);

        // When
        List<UserDto> actualUsers = userService.getAll();

        // Then
        assertEquals(actualUsers.size(), users.size());
        then(userRepository).should().findAll();
    }

    @Test
    void testCreateSuccess() {
        // Given
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Тестовый пользователь");
        userCreateDto.setUsername("user");
        userCreateDto.setEmail("test@mail.ru");
        userCreateDto.setRoles(Set.of(id));

        given(roleRepository.findAllById(Set.of(id))).willReturn(List.of(role));
        given(userRepository.save(any(User.class))).willReturn(user);

        // When
        UserDto createdUserDto = userService.create(userCreateDto);

        // Then
        assertEquals(createdUserDto.getName(), userCreateDto.getName());
        assertEquals(createdUserDto.getUsername(), userCreateDto.getUsername());
        assertEquals(createdUserDto.getEmail(), userCreateDto.getEmail());
        assertEquals(createdUserDto.isActive(), userCreateDto.isActive());
        assertEquals(createdUserDto.getRoles().stream().map(Role::getId).collect(Collectors.toSet()), userCreateDto.getRoles());

        then(roleRepository).should().findAllById(Set.of(id));
        then(userRepository).should().save(any(User.class));
    }

    @Test
    void testUpdateSuccess() {
        // Given
        Integer updatedId = 2;

        Role updatedRole = new Role()
                .setId(updatedId)
                .setName(ERole.ADMIN)
                .setDisplayName("Администратор");

        User updatedUser = new User();
        modelMapper.map(user, updatedUser);
        updatedUser
                .setUsername("admin")
                .setRoles(Set.of(updatedRole));

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("admin");
        userUpdateDto.setRoles(Set.of(updatedId));

        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(roleRepository.findAllById(Set.of(updatedId))).willReturn(List.of(updatedRole));
        given(userRepository.save(any(User.class))).willReturn(updatedUser);

        // When
        UserDto updatedUserDto = userService.update(id, userUpdateDto);

        // Then
        assertEquals(updatedUserDto.getName(), user.getName());
        assertEquals(updatedUserDto.getUsername(), userUpdateDto.getUsername());
        assertEquals(updatedUserDto.getEmail(), user.getEmail());
        assertEquals(updatedUserDto.isActive(), user.isActive());
        assertEquals(updatedUserDto.getRoles().stream().map(Role::getId).collect(Collectors.toSet()), userUpdateDto.getRoles());

        then(userRepository).should().findById(id);
        then(roleRepository).should().findAllById(Set.of(updatedId));
        then(userRepository).should().save(any(User.class));
    }

    @Test
    void testUpdateNotFound() {
        // Given
        given(userRepository.findById(id)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> userService.update(id, new UserUpdateDto()));

        // Then
        then(userRepository).should().findById(id);
        then(roleRepository).should(never()).findAllById(any());
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    void testDeleteSuccess() {
        // Given
        String username = "admin";
        User admin = new User()
                .setId(2)
                .setUsername(username);

        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(username);
        given(userRepository.findByUsernameOrEmail(username)).willReturn(Optional.of(admin));
        doNothing().when(userRepository).deleteById(id);

        // When
        userService.delete(id);

        // Then
        then(userRepository).should().findById(id);
        then(securityContext).should().getAuthentication();
        then(authentication).should().getName();
        then(userRepository).should().findByUsernameOrEmail(username);
        then(userRepository).should().deleteById(id);
    }

    @Test
    void testDeleteIllegalUser() {
        String username = "user";

        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(username);
        given(userRepository.findByUsernameOrEmail(username)).willReturn(Optional.of(user));

        // When
        assertThrows(IllegalArgumentException.class, () -> userService.delete(id));

        // Then
        then(userRepository).should().findById(id);
        then(securityContext).should().getAuthentication();
        then(authentication).should().getName();
        then(userRepository).should().findByUsernameOrEmail(username);
        then(userRepository).should(never()).deleteById(id);
    }

    @Test
    void testDeleteNotFound() {
        // Given
        given(userRepository.findById(id)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> userService.delete(id));

        // Then
        then(userRepository).should().findById(id);
        then(securityContext).should(never()).getAuthentication();
        then(authentication).should(never()).getName();
        then(userRepository).should(never()).findByUsernameOrEmail(any());
        then(userRepository).should(never()).deleteById(id);
    }

    @Test
    void testUploadAvatarSuccess() throws IOException {
        // Given
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        MultipartFile avatar = new MockMultipartFile("test.png", "test.png", MediaType.IMAGE_PNG_VALUE, baos.toByteArray());

        User updatedUser = new User();
        modelMapper.map(user, updatedUser);
        updatedUser.setAvatar(avatar.getBytes());

        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(userRepository.save(any(User.class))).willReturn(updatedUser);

        // When
        UserDto updatedUserDto = userService.uploadAvatar(id, avatar);

        // Then
        assertArrayEquals(updatedUserDto.getAvatar(), avatar.getBytes());

        then(userRepository).should().findById(id);
        then(userRepository).should().save(any(User.class));
    }

    @Test
    void testUploadAvatarIllegalFormat() throws IOException {
        // Given
        MultipartFile avatar = new MockMultipartFile("test.txt", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

        given(userRepository.findById(id)).willReturn(Optional.of(user));

        // When
        assertThrows(IllegalArgumentException.class, () -> userService.uploadAvatar(id, avatar));

        // Then
        then(userRepository).should().findById(id);
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    void testUploadAvatarNotFound() {
        // Given
        MultipartFile avatar = new MockMultipartFile("test.txt", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

        given(userRepository.findById(id)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> userService.uploadAvatar(id, avatar));

        // Then
        then(userRepository).should().findById(id);
        then(userRepository).should(never()).save(any(User.class));
    }
}