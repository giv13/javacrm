package ru.giv13.infocrm.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.giv13.infocrm.user.ERole;
import ru.giv13.infocrm.user.RoleRepository;
import ru.giv13.infocrm.user.User;
import ru.giv13.infocrm.user.UserRepository;
import ru.giv13.infocrm.user.dto.LoginRequest;
import ru.giv13.infocrm.user.dto.RegisterRequest;
import ru.giv13.infocrm.user.dto.UserDto;
import ru.giv13.infocrm.user.dto.UserToUserDtoConverter;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByUsernameOrEmail(request.username(), request.email())) {
            throw new UserAlreadyExistsException("Такой пользователь уже существует");
        }
        User.UserBuilder userBuilder = User
                .builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()));
        roleRepository.findByName(ERole.USER).ifPresent(userRole -> userBuilder.roles(Set.of(userRole)));
        User user = userBuilder.build();
        userRepository.save(user);
        jwtService.generateCookie(user);
        return userToUserDtoConverter.convert(user);
    }

    public UserDto login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsernameOrEmail(request.username()).orElseThrow();
        jwtService.generateCookie(user);
        return userToUserDtoConverter.convert(user);
    }

    public Object logout() {
        jwtService.eraseCookie();
        return null;
    }
}
