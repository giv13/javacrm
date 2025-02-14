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
    @Value("${security.jwt.expiration}")
    private int jwtExpiration;

    public AuthResponse register(RegisterRequest request) {
        User.UserBuilder userBuilder = User
                .builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()));
        roleRepository.findByName(ERole.USER).ifPresent(userRole -> userBuilder.roles(Set.of(userRole)));
        User user = userBuilder.build();
        userRepository.save(user);
        return generateToken(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsernameOrEmail(request.getUsername()).orElseThrow();
        return generateToken(user);
    }

    private AuthResponse generateToken(User user) {
        String token = jwtService.generateToken(user);
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setMaxAge(jwtExpiration);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
        return AuthResponse.builder().token(token).build();
    }
}
