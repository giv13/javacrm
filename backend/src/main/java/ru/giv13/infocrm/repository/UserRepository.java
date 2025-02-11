package ru.giv13.infocrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.giv13.infocrm.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
