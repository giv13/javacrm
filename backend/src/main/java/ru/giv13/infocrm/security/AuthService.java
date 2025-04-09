package ru.giv13.infocrm.security;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.giv13.infocrm.user.ERole;
import ru.giv13.infocrm.user.RoleRepository;
import ru.giv13.infocrm.user.User;
import ru.giv13.infocrm.user.UserRepository;
import ru.giv13.infocrm.user.dto.UserLoginDto;
import ru.giv13.infocrm.user.dto.UserProfileDto;
import ru.giv13.infocrm.user.dto.UserRegisterDto;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    @Transactional
    public UserProfileDto register(UserRegisterDto userRegisterDto) {
        User user = modelMapper.map(userRegisterDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        roleRepository.findByName(ERole.USER).ifPresent(userRole -> user.setRoles(Set.of(userRole)));
        userRepository.save(user);
        jwtService.generateCookie(user);
        return modelMapper.map(user, UserProfileDto.class);
    }

    @Transactional(readOnly = true)
    public UserProfileDto login(UserLoginDto userLoginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword()));
        User user = (User) authentication.getPrincipal();
        jwtService.generateCookie(user);
        return modelMapper.map(user, UserProfileDto.class);
    }

    public void logout() {
        jwtService.eraseCookie();
    }
}
