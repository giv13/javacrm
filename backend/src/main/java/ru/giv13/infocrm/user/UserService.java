package ru.giv13.infocrm.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.giv13.infocrm.user.dto.UserCreateDto;
import ru.giv13.infocrm.user.dto.UserDto;
import ru.giv13.infocrm.user.dto.UserUpdateDto;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь " + username + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

    @Transactional
    public UserDto create(UserCreateDto userCreateDto) {
        User user = modelMapper.map(userCreateDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findAllById(userCreateDto.getRoles())));
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Transactional
    public UserDto update(Integer id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Пользователь c id = " + id + " не найден"));
        userUpdateDto.setId(null);
        modelMapper.map(userUpdateDto, user);
        user.setRoles(new HashSet<>(roleRepository.findAllById(userUpdateDto.getRoles())));
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }
}
