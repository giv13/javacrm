package ru.giv13.javacrm.user;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.giv13.javacrm.system.ImageCropper;
import ru.giv13.javacrm.user.dto.UserCreateDto;
import ru.giv13.javacrm.user.dto.UserDto;
import ru.giv13.javacrm.user.dto.UserUpdateDto;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
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
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Transactional
    public UserDto update(Integer id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "user"));
        if (userUpdateDto.getPassword() != null) {
            userUpdateDto.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }
        modelMapper.map(userUpdateDto, user);
        if (userUpdateDto.getRoles() != null) {
            user.setRoles(new HashSet<>(roleRepository.findAllById(userUpdateDto.getRoles())));
        }
        user.setRefresh(null);
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Transactional
    public void delete(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "user"));
        User principal = loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (Objects.equals(user.getId(), principal.getId())) {
            throw new IllegalArgumentException("Вы не можете удалить самого себя");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDto uploadAvatar(Integer id, MultipartFile avatar) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "user"));
        Map<String, String> allowedTypes = Map.of("image/jpeg", "jpg", "image/png", "png");
        try {
            if (avatar == null || avatar.isEmpty()) {
                user.setAvatar(null);
            } else {
                if (!allowedTypes.containsKey(avatar.getContentType())) {
                    throw new IllegalArgumentException("Недопустимый формат изображения");
                }
                user.setAvatar(ImageCropper.cropToSquare(avatar.getBytes(), 64, allowedTypes.get(avatar.getContentType())));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка загрузки изображения");
        }
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }
}
