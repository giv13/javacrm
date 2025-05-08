package ru.giv13.javacrm.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @EntityGraph(attributePaths = "roles.permissions")
    Optional<User> findByUsernameOrEmail(String username, String email);

    default Optional<User> findByUsernameOrEmail(String username) {
        return findByUsernameOrEmail(username, username);
    };

    @EntityGraph(attributePaths = "roles.permissions")
    @NonNull
    List<User> findAll();

    @EntityGraph(attributePaths = "roles.permissions")
    @NonNull
    Optional<User> findById(@NonNull Integer id);

    boolean existsByUsernameAndIdNot(String username, Integer id);

    boolean existsByEmailAndIdNot(String email, Integer id);
}
