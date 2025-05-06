package ru.giv13.javacrm.project;

import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.giv13.javacrm.project.dto.ProjectCreateDto;
import ru.giv13.javacrm.project.dto.ProjectDto;
import ru.giv13.javacrm.project.dto.ProjectUpdateDto;
import ru.giv13.javacrm.user.User;
import ru.giv13.javacrm.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private ProjectService projectService;

    private final Integer id = 1;
    private Status status;
    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setCollectionsMergeEnabled(false);

        status = new Status()
                .setId(id)
                .setName(EStatus.NEW)
                .setDisplayName("Новый");

        user = new User()
                .setId(id);

        project = new Project()
                .setId(id)
                .setName("Тестовое название проекта")
                .setDescription("Тестовое описание проекта")
                .setStatus(status)
                .setResponsible(user)
                .setParticipants(Set.of(user));
    }

    @Test
    void testGetAllSuccess() {
        // Given
        Project copiedProject = new Project();
        modelMapper.map(project, copiedProject);
        copiedProject
                .setId(2)
                .setName("Тестовое название проекта 2")
                .setDescription("Тестовое описание проекта 2");
        List<Project> projects = List.of(project, copiedProject);

        given(projectRepository.findAll()).willReturn(projects);

        // When
        List<ProjectDto> actualProjects = projectService.getAll();

        // Then
        assertEquals(actualProjects.size(), projects.size());
        then(projectRepository).should().findAll();
    }

    @Test
    void testCreateSuccess() {
        // Given
        ProjectCreateDto projectCreateDto = new ProjectCreateDto();
        projectCreateDto.setName("Тестовое название проекта");
        projectCreateDto.setDescription("Тестовое описание проекта");
        projectCreateDto.setStatus(id);
        projectCreateDto.setResponsible(id);
        projectCreateDto.setParticipants(Set.of(id));

        given(statusRepository.findById(id)).willReturn(Optional.of(status));
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(userRepository.findAllById(Set.of(id))).willReturn(List.of(user));
        given(projectRepository.save(any(Project.class))).willReturn(project);

        // When
        ProjectDto createdProjectDto = projectService.create(projectCreateDto);

        // Then
        assertEquals(createdProjectDto.getName(), projectCreateDto.getName());
        assertEquals(createdProjectDto.getDescription(), projectCreateDto.getDescription());
        assertEquals(createdProjectDto.getStatus().getId(), projectCreateDto.getStatus());
        assertEquals(createdProjectDto.getResponsible(), projectCreateDto.getResponsible());
        assertEquals(createdProjectDto.getParticipants(), new ArrayList<>(projectCreateDto.getParticipants()));

        then(statusRepository).should().findById(id);
        then(userRepository).should().findById(id);
        then(userRepository).should().findAllById(Set.of(id));
        then(projectRepository).should().save(any(Project.class));
    }

    @Test
    void testUpdateSuccess() {
        // Given
        Integer updatedId = 2;

        Status updatedStatus = new Status()
                .setId(updatedId)
                .setName(EStatus.IN_PROGRESS)
                .setDisplayName("В работе");

        Project updatedProject = new Project();
        modelMapper.map(project, updatedProject);
        updatedProject
                .setName("Обновленное название проекта")
                .setStatus(updatedStatus);

        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto();
        projectUpdateDto.setName("Обновленное название проекта");
        projectUpdateDto.setStatus(updatedId);

        given(projectRepository.findById(id)).willReturn(Optional.of(project));
        given(statusRepository.findById(updatedId)).willReturn(Optional.of(updatedStatus));
        given(projectRepository.save(any(Project.class))).willReturn(updatedProject);

        // When
        ProjectDto updatedProjectDto = projectService.update(id, projectUpdateDto);

        // Then
        assertEquals(updatedProjectDto.getName(), projectUpdateDto.getName());
        assertEquals(updatedProjectDto.getDescription(), project.getDescription());
        assertEquals(updatedProjectDto.getStatus().getId(), projectUpdateDto.getStatus());
        assertEquals(updatedProjectDto.getResponsible(), project.getResponsible().getId());
        assertEquals(updatedProjectDto.getParticipants(), project.getParticipants().stream().map(User::getId).toList());

        then(projectRepository).should().findById(id);
        then(statusRepository).should().findById(updatedId);
        then(userRepository).should(never()).findById(any());
        then(userRepository).should(never()).findAllById(any());
        then(projectRepository).should().save(any(Project.class));
    }

    @Test
    void testUpdateSuccessNullResponsibleAndParticipants() {
        // Given
        Integer updatedId = 99;

        Project updatedProject = new Project();
        modelMapper.map(project, updatedProject);
        updatedProject
                .setResponsible(null)
                .setParticipants(Set.of());

        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto();
        projectUpdateDto.setResponsible(updatedId);
        projectUpdateDto.setParticipants(Set.of(updatedId));

        given(projectRepository.findById(id)).willReturn(Optional.of(project));
        given(userRepository.findById(updatedId)).willReturn(Optional.empty());
        given(userRepository.findAllById(Set.of(updatedId))).willReturn(List.of());
        given(projectRepository.save(any(Project.class))).willReturn(updatedProject);

        // When
        ProjectDto updatedProjectDto = projectService.update(id, projectUpdateDto);

        // Then
        assertEquals(updatedProjectDto.getName(), project.getName());
        assertEquals(updatedProjectDto.getDescription(), project.getDescription());
        assertEquals(updatedProjectDto.getStatus(), project.getStatus());
        assertNull(updatedProjectDto.getResponsible());
        assertEquals(updatedProjectDto.getParticipants(), List.of());

        then(projectRepository).should().findById(id);
        then(statusRepository).should(never()).findById(any());
        then(userRepository).should().findById(updatedId);
        then(userRepository).should().findAllById(Set.of(updatedId));
        then(projectRepository).should().save(any(Project.class));
    }

    @Test
    void testUpdateNotFoundError() {
        // Given
        given(projectRepository.findById(id)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> projectService.update(id, new ProjectUpdateDto()));

        // Then
        then(projectRepository).should().findById(id);
        then(statusRepository).should(never()).findById(any());
        then(userRepository).should(never()).findById(any());
        then(userRepository).should(never()).findAllById(any());
        then(projectRepository).should(never()).save(any(Project.class));
    }

    @Test
    void testDeleteSuccess() {
        // Given
        given(projectRepository.findById(id)).willReturn(Optional.of(project));
        doNothing().when(projectRepository).deleteById(id);

        // When
        projectService.delete(id);

        // Then
        then(projectRepository).should().findById(id);
        then(projectRepository).should().deleteById(id);
    }

    @Test
    void testDeleteNotFoundError() {
        // Given
        given(projectRepository.findById(id)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> projectService.delete(id));

        // Then
        then(projectRepository).should().findById(id);
        then(projectRepository).should(never()).deleteById(id);
    }
}