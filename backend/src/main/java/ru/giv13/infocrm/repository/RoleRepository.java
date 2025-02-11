package ru.giv13.infocrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.giv13.infocrm.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
