package com.example.petproject.mappers;

import com.example.petproject.dto.ProjectDto;
import com.example.petproject.model.Project;
import java.util.ArrayList;

public class ProjectMapper {
    private ProjectMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static ProjectDto toDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setFurnaceIds(project.getFurnaces() != null ? new ArrayList<>(project.getFurnaces())
                : null);
        return dto;
    }

    public static Project toEntity(ProjectDto dto) {
        Project project = new Project();
        project.setId(dto.getId());
        project.setName(dto.getName());
        return project; // Note: Furnaces need to be set separately in the service
    }
}
