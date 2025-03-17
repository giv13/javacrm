package ru.giv13.infocrm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь " + username + " не найден"));
    }

    public List<User> getAll(boolean withRoles) {
        return withRoles ? userRepository.findAllWithRoles() : userRepository.findAll();
    }

    public List<User> getAll() {
        return getAll(false);
    }
}
