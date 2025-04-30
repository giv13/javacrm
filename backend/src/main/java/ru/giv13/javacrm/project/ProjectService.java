package ru.giv13.javacrm.project;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.giv13.javacrm.project.dto.ProjectDto;
import ru.giv13.javacrm.project.dto.ProjectRequestDto;
import ru.giv13.javacrm.user.UserRepository;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ProjectDto> getAll() {
        return projectRepository.findAll().stream().map(project -> modelMapper.map(project, ProjectDto.class)).toList();
    }

    @Transactional
    public ProjectDto create(ProjectRequestDto projectRequestDto) {
        Project project = modelMapper.map(projectRequestDto, Project.class);
        return save(project, projectRequestDto);
    }

    @Transactional
    public ProjectDto update(Integer id, ProjectRequestDto projectRequestDto) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "project"));
        modelMapper.map(projectRequestDto, project);
        return save(project, projectRequestDto);
    }

    public void delete(Integer id) {
        projectRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "project"));
        projectRepository.deleteById(id);
    }

    private ProjectDto save(Project project, ProjectRequestDto projectRequestDto) {
        if (projectRequestDto.getStatus() != null) {
            project.setStatus(statusRepository.findById(projectRequestDto.getStatus()).orElse(null));
        }
        if (projectRequestDto.getResponsible() != null) {
            project.setResponsible(userRepository.findById(projectRequestDto.getResponsible()).orElse(null));
        }
        if (projectRequestDto.getParticipants() != null) {
            project.setParticipants(new HashSet<>(userRepository.findAllById(projectRequestDto.getParticipants())));
        }
        return modelMapper.map(projectRepository.save(project), ProjectDto.class);
    }
}
