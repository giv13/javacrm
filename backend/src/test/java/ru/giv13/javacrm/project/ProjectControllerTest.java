package ru.giv13.javacrm.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.giv13.javacrm.project.dto.ProjectCreateDto;
import ru.giv13.javacrm.project.dto.ProjectDto;
import ru.giv13.javacrm.project.dto.ProjectUpdateDto;
import ru.giv13.javacrm.system.ExceptionHandlerAdvice;
import ru.giv13.javacrm.user.User;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    @Mock
    private ProjectService projectService;

    @Spy
    private ModelMapper modelMapper;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    ProjectController projectController;

    private MockMvc mockMvc;

    private final Integer id = 1;
    private Status status;
    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).setControllerAdvice(ExceptionHandlerAdvice.class).build();

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
    void testGetAllSuccess() throws Exception {
        // Given
        Project copiedProject = new Project();
        modelMapper.map(project, copiedProject);
        copiedProject
                .setId(2)
                .setName("Тестовое название проекта 2")
                .setDescription("Тестовое описание проекта 2");
        List<Project> projects = List.of(project, copiedProject);
        List<ProjectDto> projectsDto = projects.stream().map(project -> modelMapper.map(project, ProjectDto.class)).toList();

        given(projectService.getAll()).willReturn(projectsDto);

        // When and then
        mockMvc.perform(get("/projects").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data", Matchers.hasSize(projects.size())));
    }

    @Test
    void testCreateSuccess() throws Exception {
        // Given
        ProjectCreateDto projectCreateDto = new ProjectCreateDto();
        projectCreateDto.setName("Тестовое название проекта");
        projectCreateDto.setDescription("Тестовое описание проекта");
        projectCreateDto.setStatus(id);
        projectCreateDto.setResponsible(id);
        projectCreateDto.setParticipants(Set.of(id));
        String json = objectMapper.writeValueAsString(projectCreateDto);

        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);

        given(projectService.create(projectCreateDto)).willReturn(projectDto);

        // When and then
        mockMvc.perform(post("/projects").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.name").value("Тестовое название проекта"))
                .andExpect(jsonPath("$.data.description").value("Тестовое описание проекта"))
                .andExpect(jsonPath("$.data.status.id").value(id))
                .andExpect(jsonPath("$.data.responsible").value(id))
                .andExpect(jsonPath("$.data.participants[0]").value(id));
    }

    @Test
    void testCreateValidationError() throws Exception {
        // Given
        ProjectCreateDto projectCreateDto = new ProjectCreateDto();
        String json = objectMapper.writeValueAsString(projectCreateDto);

        // When and then
        mockMvc.perform(post("/projects").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error.name").isNotEmpty())
                .andExpect(jsonPath("$.error.status").isNotEmpty())
                .andExpect(jsonPath("$.error.responsible").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUpdateSuccess() throws Exception {
        // Given
        Integer updatedId = 2;

        User updatedUser = new User()
                .setId(updatedId);

        Project updatedProject = new Project();
        modelMapper.map(project, updatedProject);
        updatedProject
                .setName("Обновленное название проекта")
                .setResponsible(updatedUser);
        ProjectDto projectDto = modelMapper.map(updatedProject, ProjectDto.class);

        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto();
        projectUpdateDto.setName("Обновленное название проекта");
        projectUpdateDto.setResponsible(updatedId);
        String json = objectMapper.writeValueAsString(projectUpdateDto);

        given(projectService.update(id, projectUpdateDto)).willReturn(projectDto);

        // When and then
        mockMvc.perform(put("/projects/" + id).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data.name").value("Обновленное название проекта"))
                .andExpect(jsonPath("$.data.description").value("Тестовое описание проекта"))
                .andExpect(jsonPath("$.data.status.id").value(id))
                .andExpect(jsonPath("$.data.responsible").value(updatedId))
                .andExpect(jsonPath("$.data.participants[0]").value(id));;
    }

    @Test
    void testUpdateValidationError() throws Exception {
        // Given
        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto();
        projectUpdateDto.setName("");
        String json = objectMapper.writeValueAsString(projectUpdateDto);

        // When and then
        mockMvc.perform(put("/projects/" + id).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error.name").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUpdateNotFoundError() throws Exception {
        // Given
        ProjectUpdateDto projectUpdateDto = new ProjectUpdateDto();
        String json = objectMapper.writeValueAsString(projectUpdateDto);

        doThrow(new ObjectNotFoundException(id, "project")).when(projectService).update(id, projectUpdateDto);

        // When and then
        mockMvc.perform(put("/projects/" + id).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteSuccess() throws Exception {
        // Given
        doNothing().when(projectService).delete(id);

        // When and then
        mockMvc.perform(delete("/projects/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteNotFoundError() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException(id, "project")).when(projectService).delete(id);

        // When and then
        mockMvc.perform(delete("/projects/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}