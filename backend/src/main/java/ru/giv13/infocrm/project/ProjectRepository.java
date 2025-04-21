package ru.giv13.infocrm.project;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @EntityGraph(attributePaths = { "status", "participants" })
    @NonNull
    List<Project> findAll();

    @EntityGraph(attributePaths = "status")
    @NonNull
    Optional<Project> findById(@NonNull Integer id);
}
