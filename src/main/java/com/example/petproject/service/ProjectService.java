package com.example.petproject.service;

import com.example.petproject.dto.ProjectDto;
import com.example.petproject.mappers.ProjectMapper;
import com.example.petproject.model.Project;
import com.example.petproject.repository.ProjectRepository;
import com.example.petproject.utils.BadRequestException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProjectService {
    private ProjectRepository projectRepository;

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(Long id) {
        if (id == null) {
            throw new BadRequestException("Project id must not be null");
        }
        return projectRepository.findById(id)
                .map(ProjectMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + id));
    }

    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new BadRequestException("Project data must not be null");
        }
        if (projectDto.getName() == null || projectDto.getName().isEmpty()) {
            throw new BadRequestException("Project name must not be null or empty");
        }
        Project project = ProjectMapper.toEntity(projectDto);
        Project savedProject = projectRepository.save(project);
        return ProjectMapper.toDto(savedProject);
    }

    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        if (id == null) {
            throw new BadRequestException("Project id must not be null");
        }
        if (projectDto == null) {
            throw new BadRequestException("Project data must not be null");
        }
        if (projectDto.getName() == null || projectDto.getName().isEmpty()) {
            throw new BadRequestException("Project name must not be null or empty");
        }
        return projectRepository.findById(id).map(project -> {
            project.setName(projectDto.getName());
            Project updatedProject = projectRepository.save(project);
            return ProjectMapper.toDto(updatedProject);
        }).orElseThrow(() -> new RuntimeException("Project not found with id " + id));
    }

    public void deleteProject(Long id) {
        if (id == null) {
            throw new BadRequestException("Project id must not be null");
        }
        projectRepository.deleteById(id);
    }
}
