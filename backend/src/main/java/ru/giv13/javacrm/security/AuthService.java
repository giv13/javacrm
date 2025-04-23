package ru.giv13.javacrm.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.giv13.javacrm.user.*;
import ru.giv13.javacrm.user.dto.UserLoginDto;
import ru.giv13.javacrm.user.dto.UserProfileDto;
import ru.giv13.javacrm.user.dto.UserRegisterDto;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    @Value("${security.jwt.refresh.token-name}")
    private String jwtRefreshTokenName;

    @Transactional
    public UserProfileDto register(UserRegisterDto userRegisterDto) {
        User user = modelMapper.map(userRegisterDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        roleRepository.findByName(ERole.USER).ifPresent(userRole -> user.setRoles(Set.of(userRole)));
        return auth(user);
    }

    @Transactional
    public UserProfileDto login(UserLoginDto userLoginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword()));
        User user = (User) authentication.getPrincipal();
        return auth(user);
    }

    private UserProfileDto auth(User user) {
        user.setRefresh(jwtService.generateCookie(user));
        userRepository.save(user);
        return modelMapper.map(user, UserProfileDto.class);
    }

    public void refresh(HttpServletRequest request) throws JwtException {
        String refreshToken = jwtService.getCookie(request, jwtRefreshTokenName);
        if (refreshToken != null) {
            String username = jwtService.extractUsername(refreshToken);
            if (username != null) {
                User user = userService.loadUserByUsername(username);
                if (jwtService.isTokenValid(refreshToken, user) && refreshToken.equals(user.getRefresh())) {
                    user.setRefresh(jwtService.generateCookie(user));
                    userRepository.save(user);
                    return;
                }
            }
        }
        throw new JwtException("Просроченный или недействительный Refresh-токен");
    }

    public void logout() {
        jwtService.eraseCookie();
    }
}
