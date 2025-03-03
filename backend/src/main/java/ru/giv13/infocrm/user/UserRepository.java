package ru.giv13.infocrm.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameOrEmail(String username, String email);

    default Optional<User> findByUsernameOrEmail(String username) {
        return findByUsernameOrEmail(username, username);
    };

    boolean existsByUsernameOrEmail(String username, String email);
}
