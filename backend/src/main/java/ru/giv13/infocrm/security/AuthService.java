package ru.giv13.infocrm.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.giv13.infocrm.user.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User register(RegisterRequest request) {
        if (userRepository.existsByUsernameOrEmail(request.username(), request.email())) {
            throw new UserAlreadyExistsException("Такой пользователь уже существует");
        }
        User user = (new User())
                .setUsername(request.username())
                .setEmail(request.email())
                .setPassword(passwordEncoder.encode(request.password()));
        roleRepository.findByName(ERole.USER).ifPresent(userRole -> user.setRoles(List.of(userRole)));
        userRepository.save(user);
        jwtService.generateCookie(user);
        return user;
    }

    public User login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsernameOrEmail(request.username()).orElseThrow();
        jwtService.generateCookie(user);
        return user;
    }

    public void logout() {
        jwtService.eraseCookie();
    }
}
