package ru.giv13.infocrm.project;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.giv13.infocrm.project.dto.ProjectDto;
import ru.giv13.infocrm.project.dto.ProjectRequestDto;
import ru.giv13.infocrm.user.UserRepository;

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
        project.setStatus(statusRepository.findById(projectRequestDto.getStatus()).orElse(null));
        project.setResponsible(userRepository.findById(projectRequestDto.getResponsible()).orElse(null));
        project.setParticipants(new HashSet<>(userRepository.findAllById(projectRequestDto.getParticipants())));
        projectRepository.save(project);
        return modelMapper.map(project, ProjectDto.class);
    }
}
