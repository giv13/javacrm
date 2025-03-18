package ru.giv13.infocrm.project;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @EntityGraph(attributePaths = {"status", "team"})
    @NonNull
    List<Project> findAll();
}
