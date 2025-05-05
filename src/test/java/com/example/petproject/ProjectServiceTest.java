package com.example.petproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.petproject.dto.ProjectDto;
import com.example.petproject.mappers.ProjectMapper;
import com.example.petproject.model.Project;
import com.example.petproject.repository.ProjectRepository;
import com.example.petproject.service.ProjectService;
import com.example.petproject.utils.BadRequestException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void testGetAllProjects() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));

        try (MockedStatic<ProjectMapper> mapperMock = mockStatic(ProjectMapper.class)) {
            ProjectDto dto = new ProjectDto();
            dto.setId(1L);
            dto.setName("Test Project");
            mapperMock.when(() -> ProjectMapper.toDto(project)).thenReturn(dto);

            List<ProjectDto> result = projectService.getAllProjects();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Project", result.get(0).getName());
        }
    }

    @Test
    void testGetProjectById_NullId() {
        Exception ex = assertThrows(BadRequestException.class,
                () -> projectService.getProjectById(null));
        assertEquals("Project id must not be null", ex.getMessage());
    }

    @Test
    void testGetProjectById_NotFound() {
        Long id = 1L;
        when(projectRepository.findById(id)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class,
                () -> projectService.getProjectById(id));
        assertEquals("Project not found with id " + id, ex.getMessage());
    }

    @Test
    void testGetProjectById_Success() {
        Long id = 1L;
        Project project = new Project();
        project.setId(id);
        project.setName("Test Project");
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));

        try (MockedStatic<ProjectMapper> mapperMock = mockStatic(ProjectMapper.class)) {
            ProjectDto dto = new ProjectDto();
            dto.setId(id);
            dto.setName("Test Project");
            mapperMock.when(() -> ProjectMapper.toDto(project)).thenReturn(dto);

            ProjectDto result = projectService.getProjectById(id);
            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals("Test Project", result.getName());
        }
    }

    @Test
    void testCreateProject_NullDto() {
        Exception ex = assertThrows(BadRequestException.class,
                () -> projectService.createProject(null));
        assertEquals("Project data must not be null", ex.getMessage());
    }

    @Test
    void testCreateProject_EmptyName() {
        ProjectDto dto = new ProjectDto();
        dto.setName("");
        Exception ex = assertThrows(BadRequestException.class,
                () -> projectService.createProject(dto));
        assertEquals("Project name must not be null or empty", ex.getMessage());
    }

    @Test
    void testCreateProject_Success() {
        ProjectDto inputDto = new ProjectDto();
        inputDto.setName("New Project");

        Project projectEntity = new Project();
        projectEntity.setName("New Project");

        Project savedProject = new Project();
        savedProject.setId(1L);
        savedProject.setName("New Project");

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        try (MockedStatic<ProjectMapper> mapperMock = mockStatic(ProjectMapper.class)) {
            mapperMock.when(() -> ProjectMapper.toEntity(inputDto)).thenReturn(projectEntity);

            ProjectDto outputDto = new ProjectDto();
            outputDto.setId(1L);
            outputDto.setName("New Project");
            mapperMock.when(() -> ProjectMapper.toDto(savedProject)).thenReturn(outputDto);

            ProjectDto result = projectService.createProject(inputDto);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("New Project", result.getName());
        }
    }

    @Test
    void testUpdateProject_NullId() {
        ProjectDto dto = new ProjectDto();
        dto.setName("Updated Project");
        Exception ex = assertThrows(BadRequestException.class,
                () -> projectService.updateProject(null, dto));
        assertEquals("Project id must not be null", ex.getMessage());
    }

    @Test
    void testUpdateProject_NullDto() {
        Exception ex = assertThrows(BadRequestException.class,
                () -> projectService.updateProject(1L, null));
        assertEquals("Project data must not be null", ex.getMessage());
    }

    @Test
    void testUpdateProject_EmptyName() {
        ProjectDto dto = new ProjectDto();
        dto.setName("");
        Exception ex = assertThrows(BadRequestException.class,
                () -> projectService.updateProject(1L, dto));
        assertEquals("Project name must not be null or empty", ex.getMessage());
    }

    @Test
    void testUpdateProject_NotFound() {
        ProjectDto dto = new ProjectDto();
        dto.setName("Updated Project");
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class,
                () -> projectService.updateProject(1L, dto));
        assertEquals("Project not found with id 1", ex.getMessage());
    }

    @Test
    void testUpdateProject_Success() {
        Long id = 1L;
        ProjectDto inputDto = new ProjectDto();
        inputDto.setName("Updated Project");

        Project project = new Project();
        project.setId(id);
        project.setName("Old Project");

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));

        Project updatedProject = new Project();
        updatedProject.setId(id);
        updatedProject.setName("Updated Project");
        when(projectRepository.save(project)).thenReturn(updatedProject);

        try (MockedStatic<ProjectMapper> mapperMock = mockStatic(ProjectMapper.class)) {
            ProjectDto outputDto = new ProjectDto();
            outputDto.setId(id);
            outputDto.setName("Updated Project");
            mapperMock.when(() -> ProjectMapper.toDto(updatedProject)).thenReturn(outputDto);

            ProjectDto result = projectService.updateProject(id, inputDto);
            assertNotNull(result);
            assertEquals("Updated Project", result.getName());
        }
    }

    @Test
    void testDeleteProject_NullId() {
        Exception ex = assertThrows(BadRequestException.class,
                () -> projectService.deleteProject(null));
        assertEquals("Project id must not be null", ex.getMessage());
    }

    @Test
    void testDeleteProject_Success() {
        Long id = 1L;
        doNothing().when(projectRepository).deleteById(id);
        projectService.deleteProject(id);
        verify(projectRepository, times(1)).deleteById(id);
    }
}
