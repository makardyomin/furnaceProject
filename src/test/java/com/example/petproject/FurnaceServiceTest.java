package com.example.petproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.petproject.dto.FurnaceDto;
import com.example.petproject.model.Furnace;
import com.example.petproject.model.Project;
import com.example.petproject.repository.FurnaceRepository;
import com.example.petproject.repository.MaterialRepository;
import com.example.petproject.repository.ProjectRepository;
import com.example.petproject.service.FurnaceService;
import com.example.petproject.utils.BadRequestException;
import com.example.petproject.mappers.FurnaceMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FurnaceServiceTest {

    @Mock
    private FurnaceRepository furnaceRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private FurnaceService furnaceService;

    @Test
    void testGetAllFurnaces() {
        Furnace furnace = new Furnace();
        furnace.setId(1L);
        furnace.setType("TestType");
        List<Furnace> furnaces = Collections.singletonList(furnace);
        when(furnaceRepository.findAll()).thenReturn(furnaces);

        try (MockedStatic<FurnaceMapper> mapperMock = mockStatic(FurnaceMapper.class)) {
            FurnaceDto dto = new FurnaceDto();
            dto.setId(1L);
            dto.setType("TestType");
            mapperMock.when(() -> FurnaceMapper.toDto(furnace)).thenReturn(dto);

            List<FurnaceDto> result = furnaceService.getAllFurnaces();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("TestType", result.get(0).getType());
        }
    }

    @Test
    void testGetFurnaceById_NullId() {
        Exception exception = assertThrows(BadRequestException.class, () -> furnaceService.getFurnaceById(null));
        assertEquals("Furnace id must not be null", exception.getMessage());
    }

    @Test
    void testGetFurnaceById_NotFound() {
        Long id = 1L;
        when(furnaceRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> furnaceService.getFurnaceById(id));
        assertEquals("Furnace not found with id " + id, exception.getMessage());
    }

    @Test
    void testGetFurnaceById_Success() {
        Long id = 1L;
        Furnace furnace = new Furnace();
        furnace.setId(id);
        furnace.setType("TestType");

        when(furnaceRepository.findById(id)).thenReturn(Optional.of(furnace));

        try (MockedStatic<FurnaceMapper> mapperMock = mockStatic(FurnaceMapper.class)) {
            FurnaceDto dto = new FurnaceDto();
            dto.setId(id);
            dto.setType("TestType");
            mapperMock.when(() -> FurnaceMapper.toDto(furnace)).thenReturn(dto);

            FurnaceDto result = furnaceService.getFurnaceById(id);
            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals("TestType", result.getType());
        }
    }

    @Test
    void testCreateFurnace_NullDto() {
        Exception exception = assertThrows(BadRequestException.class, () -> furnaceService.createFurnace(null));
        assertEquals("Furnace data must not be null", exception.getMessage());
    }

    @Test
    void testCreateFurnace_EmptyType() {
        FurnaceDto dto = new FurnaceDto();
        dto.setType("");
        Exception exception = assertThrows(BadRequestException.class, () -> furnaceService.createFurnace(dto));
        assertEquals("Furnace type must not be null or empty", exception.getMessage());
    }

    @Test
    void testCreateFurnace_Success() {
        FurnaceDto inputDto = new FurnaceDto();
        inputDto.setType("Type1");

        Furnace furnaceEntity = new Furnace();
        furnaceEntity.setType("Type1");

        Furnace savedFurnace = new Furnace();
        savedFurnace.setId(10L);
        savedFurnace.setType("Type1");

        when(furnaceRepository.save(any(Furnace.class))).thenReturn(savedFurnace);

        try (MockedStatic<FurnaceMapper> mapperMock = mockStatic(FurnaceMapper.class)) {
            // Stub conversion methods
            mapperMock.when(() -> FurnaceMapper.toEntity(inputDto)).thenReturn(furnaceEntity);

            FurnaceDto outputDto = new FurnaceDto();
            outputDto.setId(10L);
            outputDto.setType("Type1");
            mapperMock.when(() -> FurnaceMapper.toDto(savedFurnace)).thenReturn(outputDto);

            FurnaceDto result = furnaceService.createFurnace(inputDto);
            assertNotNull(result);
            assertEquals(10L, result.getId());
            assertEquals("Type1", result.getType());
        }
    }

    @Test
    void testUpdateFurnace_NullId() {
        FurnaceDto dto = new FurnaceDto();
        dto.setType("Type1");
        Exception exception = assertThrows(BadRequestException.class, () -> furnaceService.updateFurnace(null, dto));
        assertEquals("Furnace id must not be null", exception.getMessage());
    }

    @Test
    void testUpdateFurnace_NullDto() {
        Exception exception = assertThrows(BadRequestException.class, () -> furnaceService.updateFurnace(1L, null));
        assertEquals("Furnace data must not be null", exception.getMessage());
    }

    @Test
    void testUpdateFurnace_EmptyType() {
        FurnaceDto dto = new FurnaceDto();
        dto.setType("");
        Exception exception = assertThrows(BadRequestException.class, () -> furnaceService.updateFurnace(1L, dto));
        assertEquals("Furnace type must not be null or empty", exception.getMessage());
    }

    @Test
    void testUpdateFurnace_NotFound() {
        FurnaceDto dto = new FurnaceDto();
        dto.setType("Type1");

        when(furnaceRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> furnaceService.updateFurnace(1L, dto));
        assertEquals("Furnace not found with id 1", exception.getMessage());
    }

    @Test
    void testUpdateFurnace_UpdateWithoutProject() {
        Long id = 1L;
        FurnaceDto inputDto = new FurnaceDto();
        inputDto.setType("NewType");
        inputDto.setProjectId(null);

        Furnace existingFurnace = new Furnace();
        existingFurnace.setId(id);
        existingFurnace.setType("OldType");

        when(furnaceRepository.findById(id)).thenReturn(Optional.of(existingFurnace));

        Furnace updatedFurnace = new Furnace();
        updatedFurnace.setId(id);
        updatedFurnace.setType("NewType");

        when(furnaceRepository.save(existingFurnace)).thenReturn(updatedFurnace);

        try (MockedStatic<FurnaceMapper> mapperMock = mockStatic(FurnaceMapper.class)) {
            FurnaceDto outputDto = new FurnaceDto();
            outputDto.setId(id);
            outputDto.setType("NewType");
            mapperMock.when(() -> FurnaceMapper.toDto(updatedFurnace)).thenReturn(outputDto);

            FurnaceDto result = furnaceService.updateFurnace(id, inputDto);
            assertNotNull(result);
            assertEquals("NewType", result.getType());
            verify(furnaceRepository, times(1)).findById(id);
            verify(furnaceRepository, times(1)).save(existingFurnace);
        }
    }

    @Test
    void testUpdateFurnace_UpdateWithProject() {
        Long id = 1L;
        FurnaceDto inputDto = new FurnaceDto();
        inputDto.setType("NewType");
        inputDto.setProjectId(100L);

        Furnace existingFurnace = new Furnace();
        existingFurnace.setId(id);
        existingFurnace.setType("OldType");

        when(furnaceRepository.findById(id)).thenReturn(Optional.of(existingFurnace));

        Project project = new Project();
        project.setId(100L);
        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        Furnace updatedFurnace = new Furnace();
        updatedFurnace.setId(id);
        updatedFurnace.setType("NewType");
        updatedFurnace.setProject(project);

        when(furnaceRepository.save(existingFurnace)).thenReturn(updatedFurnace);

        try (MockedStatic<FurnaceMapper> mapperMock = mockStatic(FurnaceMapper.class)) {
            FurnaceDto outputDto = new FurnaceDto();
            outputDto.setId(id);
            outputDto.setType("NewType");
            outputDto.setProjectId(100L);
            mapperMock.when(() -> FurnaceMapper.toDto(updatedFurnace)).thenReturn(outputDto);

            FurnaceDto result = furnaceService.updateFurnace(id, inputDto);
            assertNotNull(result);
            assertEquals("NewType", result.getType());
            assertEquals(100L, result.getProjectId());
        }
    }

    @Test
    void testDeleteFurnace_NullId() {
        Exception exception = assertThrows(BadRequestException.class, () -> furnaceService.deleteFurnace(null));
        assertEquals("Furnace id must not be null", exception.getMessage());
    }

    @Test
    void testDeleteFurnace_Success() {
        Long id = 1L;
        doNothing().when(furnaceRepository).deleteById(id);
        furnaceService.deleteFurnace(id);
        verify(furnaceRepository, times(1)).deleteById(id);
    }
}

