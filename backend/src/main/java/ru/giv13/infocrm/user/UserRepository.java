package ru.giv13.infocrm.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameOrEmail(String username, String email);

    default Optional<User> findByUsernameOrEmail(String username) {
        return findByUsernameOrEmail(username, username);
    };

    @EntityGraph(attributePaths = "roles")
    @NonNull
    List<User> findAll();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
