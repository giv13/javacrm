package ru.giv13.infocrm.project;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.giv13.infocrm.project.dto.ProjectDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public List<ProjectDto> getAll() {
        return projectRepository.findAll().stream().map(project -> modelMapper.map(project, ProjectDto.class)).toList();
    }
}
