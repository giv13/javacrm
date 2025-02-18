package ru.giv13.infocrm.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final HttpServletResponse httpServletResponse;
    private final UserToUserDtoConverter userToUserDtoConverter;
    @Value("${security.jwt.expiration}")
    private Duration jwtExpiration;
    @Value("${security.jwt.token-name}")
    private String tokenName;

    public UserDto register(RegisterRequest request) {
        User.UserBuilder userBuilder = User
                .builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()));
        roleRepository.findByName(ERole.USER).ifPresent(userRole -> userBuilder.roles(Set.of(userRole)));
        User user = userBuilder.build();
        userRepository.save(user);
        return generateToken(user);
    }

    public UserDto login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsernameOrEmail(request.username()).orElseThrow();
        return generateToken(user);
    }

    private UserDto generateToken(User user) {
        String token = jwtService.generateToken(user);
        Cookie cookie = new Cookie(tokenName, token);
        cookie.setMaxAge((int) jwtExpiration.toSeconds());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
        return userToUserDtoConverter.convert(user);
    }
}
