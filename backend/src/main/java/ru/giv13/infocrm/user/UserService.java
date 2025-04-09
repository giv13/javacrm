package ru.giv13.infocrm.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.giv13.infocrm.security.UserAlreadyExistsException;
import ru.giv13.infocrm.user.dto.UserCreateDto;
import ru.giv13.infocrm.user.dto.UserDto;
import ru.giv13.infocrm.user.dto.UserUpdateDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
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

    public UserDto create(UserCreateDto userCreateDto) {
        if (userRepository.existsByUsername(userCreateDto.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь " + userCreateDto.getUsername() + " уже существует");
        }
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь " + userCreateDto.getEmail() + " уже существует");
        }
        User user = modelMapper.map(userCreateDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Transactional
    public UserDto update(Integer id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Пользователь c id = " + id + " не найден"));
        if (userUpdateDto.getUsername() != null && !userUpdateDto.getUsername().equals(user.getUsername()) && userRepository.existsByUsername(userUpdateDto.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь " + userUpdateDto.getUsername() + " уже существует");
        }
        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(userUpdateDto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь " + userUpdateDto.getEmail() + " уже существует");
        }
        modelMapper.map(userUpdateDto, user);
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }
}
