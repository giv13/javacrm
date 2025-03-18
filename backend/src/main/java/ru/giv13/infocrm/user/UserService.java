package ru.giv13.infocrm.user;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь " + username + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Hibernate.initialize(user.getProjectIds());
        }
        return users;
    }
}
