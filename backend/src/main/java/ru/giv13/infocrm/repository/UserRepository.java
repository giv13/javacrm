package ru.giv13.infocrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.giv13.infocrm.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
