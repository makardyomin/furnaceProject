package com.example.petproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.petproject.cache.Cache;
import com.example.petproject.dto.MaterialDto;
import com.example.petproject.mappers.MaterialMapper;
import com.example.petproject.model.Furnace;
import com.example.petproject.model.Material;
import com.example.petproject.repository.MaterialRepository;
import com.example.petproject.service.MaterialService;
import com.example.petproject.utils.BadRequestException;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private Cache<List<Material>> cache;

    @InjectMocks
    private MaterialService materialService;

    @Test
    void testGetAllMaterials() {
        Material material = new Material();
        material.setId(1L);
        material.setName("Material1");
        List<Material> materials = Collections.singletonList(material);
        when(materialRepository.findAll()).thenReturn(materials);

        try (MockedStatic<MaterialMapper> mapperMock = mockStatic(MaterialMapper.class)) {
            MaterialDto dto = new MaterialDto();
            dto.setId(1L);
            dto.setName("Material1");
            mapperMock.when(() -> MaterialMapper.toDto(material)).thenReturn(dto);

            List<MaterialDto> result = materialService.getAllMaterials();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Material1", result.get(0).getName());
        }
    }

    @Test
    void testGetMaterialById_NullId() {
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.getMaterialById(null));
        assertEquals("Material id must not be null", ex.getMessage());
    }

    @Test
    void testGetMaterialById_NotFound() {
        Long id = 1L;
        when(materialRepository.findById(id)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () ->
                materialService.getMaterialById(id));
        assertEquals("Material not found with id" + id, ex.getMessage());
    }

    @Test
    void testGetMaterialById_Success() {
        Long id = 1L;
        Material material = new Material();
        material.setId(id);
        material.setName("Material1");
        when(materialRepository.findById(id)).thenReturn(Optional.of(material));

        try (MockedStatic<MaterialMapper> mapperMock = mockStatic(MaterialMapper.class)) {
            MaterialDto dto = new MaterialDto();
            dto.setId(id);
            dto.setName("Material1");
            mapperMock.when(() -> MaterialMapper.toDto(material)).thenReturn(dto);

            MaterialDto result = materialService.getMaterialById(id);
            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals("Material1", result.getName());
        }
    }

    @Test
    void testFindByFurnaceType_NullType() {
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.findByFurnaceType(null));
        assertEquals("Необходимо указать параметр поиска!", ex.getMessage());
    }

    @Test
    void testFindByFurnaceType_EmptyType() {
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.findByFurnaceType(""));
        assertEquals("Необходимо указать параметр поиска!", ex.getMessage());
    }

    @Test
    void testFindByFurnaceType_FromCache() {
        String type = "Type1";
        Material material = new Material();
        material.setId(1L);
        material.setName("Material1");
        List<Furnace> furnaces = Collections.singletonList(new Furnace());
        material.setFurnaces(furnaces);
        List<Material> cachedList = Collections.singletonList(material);
        when(cache.get(type)).thenReturn(cachedList);

        List<MaterialDto> result = materialService.findByFurnaceType(type);
        assertNotNull(result);
        assertEquals(1, result.size());
        // Since the mapping uses 'new MaterialDto(material)', we assume the DTO is built from material.
    }

    @Test
    void testFindByFurnaceType_FromRepository() {
        String type = "Type2";
        when(cache.get(type)).thenReturn(null);
        Material material = new Material();
        material.setId(2L);
        material.setName("Material2");
        List<Furnace> furnaces = Collections.singletonList(new Furnace());
        material.setFurnaces(furnaces);
        List<Material> repoList = Collections.singletonList(material);
        when(materialRepository.findByFurnaceType(type)).thenReturn(repoList);

        List<MaterialDto> result = materialService.findByFurnaceType(type);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cache, times(1)).trackKey(material.getId(), type);
        verify(cache, times(1)).put(type, repoList);
    }

    @Test
    void testFindByFurnaceType_NotFound() {
        String type = "Type3";
        when(cache.get(type)).thenReturn(null);
        when(materialRepository.findByFurnaceType(type)).thenReturn(Collections.emptyList());
        Exception ex = assertThrows(RuntimeException.class, () ->
                materialService.findByFurnaceType(type));
        assertEquals("Material not found with type " + type, ex.getMessage());
    }

    @Test
    void testCreateMaterial_NullDto() {
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.createMaterial(null));
        assertEquals("Material data must not be null", ex.getMessage());
    }

    @Test
    void testCreateMaterial_EmptyName() {
        MaterialDto dto = new MaterialDto();
        dto.setName("");
        dto.setCost(10L);
        dto.setThermalInsulation("Good");
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.createMaterial(dto));
        assertEquals("Material name must not be null or empty", ex.getMessage());
    }

    @Test
    void testCreateMaterial_NullCost() {
        MaterialDto dto = new MaterialDto();
        dto.setName("TestMaterial");
        dto.setCost(null);
        dto.setThermalInsulation("Good");
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.createMaterial(dto));
        assertEquals("Material cost must not be null", ex.getMessage());
    }

    @Test
    void testCreateMaterial_EmptyThermalInsulation() {
        MaterialDto dto = new MaterialDto();
        dto.setName("TestMaterial");
        dto.setCost(10L);
        dto.setThermalInsulation("");
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.createMaterial(dto));
        assertEquals("Material type of thermal insulation must not be null or empty", ex.getMessage());
    }

    @Test
    void testCreateMaterial_Success() {
        MaterialDto inputDto = new MaterialDto();
        inputDto.setName("TestMaterial");
        inputDto.setCost(10L);
        inputDto.setThermalInsulation("Good");

        Material entity = new Material();
        entity.setName("TestMaterial");
        entity.setCost(10L);
        entity.setThermalInsulation("Good");

        Material savedEntity = new Material();
        savedEntity.setId(1L);
        savedEntity.setName("TestMaterial");
        savedEntity.setCost(10L);
        savedEntity.setThermalInsulation("Good");

        when(materialRepository.save(any(Material.class))).thenReturn(savedEntity);

        try (MockedStatic<MaterialMapper> mapperMock = mockStatic(MaterialMapper.class)) {
            mapperMock.when(() -> MaterialMapper.toEntity(inputDto)).thenReturn(entity);

            MaterialDto outputDto = new MaterialDto();
            outputDto.setId(1L);
            outputDto.setName("TestMaterial");
            outputDto.setCost(10L);
            outputDto.setThermalInsulation("Good");
            mapperMock.when(() -> MaterialMapper.toDto(savedEntity)).thenReturn(outputDto);

            MaterialDto result = materialService.createMaterial(inputDto);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("TestMaterial", result.getName());
            assertEquals(10L, result.getCost());
            assertEquals("Good", result.getThermalInsulation());
        }
    }

    @Test
    void testUpdateMaterial_NullId() {
        MaterialDto dto = new MaterialDto();
        dto.setName("Updated");
        dto.setCost(20L);
        dto.setThermalInsulation("Better");
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.updateMaterial(null, dto));
        assertEquals("Material id must not be null", ex.getMessage());
    }

    @Test
    void testUpdateMaterial_NullDto() {
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.updateMaterial(1L, null));
        assertEquals("Material data must not be null", ex.getMessage());
    }

    @Test
    void testUpdateMaterial_EmptyName() {
        MaterialDto dto = new MaterialDto();
        dto.setName("");
        dto.setCost(20L);
        dto.setThermalInsulation("Better");
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.updateMaterial(1L, dto));
        assertEquals("Material name must not be null or empty for update", ex.getMessage());
    }

    @Test
    void testUpdateMaterial_NullCost() {
        MaterialDto dto = new MaterialDto();
        dto.setName("Updated");
        dto.setCost(null);
        dto.setThermalInsulation("Better");
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.updateMaterial(1L, dto));
        assertEquals("Material cost must not be null", ex.getMessage());
    }

    @Test
    void testUpdateMaterial_EmptyThermalInsulation() {
        MaterialDto dto = new MaterialDto();
        dto.setName("Updated");
        dto.setCost(20L);
        dto.setThermalInsulation("");
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.updateMaterial(1L, dto));
        assertEquals("Material type of thermal insulation must not be null or empty", ex.getMessage());
    }

    @Test
    void testUpdateMaterial_NotFound() {
        MaterialDto dto = new MaterialDto();
        dto.setName("Updated");
        dto.setCost(20L);
        dto.setThermalInsulation("Better");
        when(materialRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () ->
                materialService.updateMaterial(1L, dto));
        assertEquals("Material not found with id1", ex.getMessage());
    }

    @Test
    void testUpdateMaterial_Success() {
        Long id = 1L;
        MaterialDto inputDto = new MaterialDto();
        inputDto.setName("Updated");
        inputDto.setCost(20L);
        inputDto.setThermalInsulation("Better");

        Material material = new Material();
        material.setId(id);
        material.setName("OldName");
        material.setCost(10L);
        material.setThermalInsulation("Good");

        when(materialRepository.findById(id)).thenReturn(Optional.of(material));

        Material updatedEntity = new Material();
        updatedEntity.setId(id);
        updatedEntity.setName("Updated");
        updatedEntity.setCost(20L);
        updatedEntity.setThermalInsulation("Better");
        when(materialRepository.save(material)).thenReturn(updatedEntity);

        Set<String> keys = new HashSet<>();
        keys.add("key1");
        when(cache.getKeys(id)).thenReturn(keys);

        List<Material> cacheList = new ArrayList<>();
        cacheList.add(material);
        when(cache.get("key1")).thenReturn(cacheList);

        try (MockedStatic<MaterialMapper> mapperMock = mockStatic(MaterialMapper.class)) {
            MaterialDto outputDto = new MaterialDto();
            outputDto.setId(id);
            outputDto.setName("Updated");
            outputDto.setCost(20L);
            outputDto.setThermalInsulation("Better");
            mapperMock.when(() -> MaterialMapper.toDto(updatedEntity)).thenReturn(outputDto);

            MaterialDto result = materialService.updateMaterial(id, inputDto);
            assertNotNull(result);
            assertEquals("Updated", result.getName());
            // Verify that the cached list now contains the updated entity instead of the old one.
            assertTrue(cacheList.contains(updatedEntity));
            assertFalse(cacheList.contains(material));
        }
    }

    @Test
    void testDeleteMaterial_NullId() {
        Exception ex = assertThrows(BadRequestException.class, () ->
                materialService.deleteMaterial(null));
        assertEquals("Material id must not be null", ex.getMessage());
    }

    @Test
    void testDeleteMaterial_NotFound() {
        when(materialRepository.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () ->
                materialService.deleteMaterial(1L));
        assertEquals("Material not found with id1", ex.getMessage());
    }

    @Test
    void testDeleteMaterial_Success() {
        Long id = 1L;
        Material material = new Material();
        material.setId(id);
        // Simulate that material has a set of furnaces (can be empty for testing)
        List<Material> materials = new ArrayList<>();
        materials.add(material);
        List<Furnace> furnaces = new ArrayList<>();
        furnaces.add(new Furnace(0L, "name", "Камерная", "Окислительная", null , materials ));
        material.setFurnaces(furnaces);
        when(materialRepository.findById(id)).thenReturn(Optional.of(material));

        doNothing().when(materialRepository).deleteById(id);
        doNothing().when(cache).remove(id);

        materialService.deleteMaterial(id);

        verify(materialRepository, times(1)).deleteById(id);
        verify(cache, times(1)).remove(id);
    }

    @Test
    void testCreateMaterials_NullList() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> materialService.createMaterials(null));
        assertEquals("Material list must not be null", ex.getMessage());
    }

    @Test
    void testCreateMaterials_EmptyList() {
        List<MaterialDto> result = materialService.createMaterials(Collections.emptyList());
        assertTrue(result.isEmpty(), "An empty list should be returned when no input is provided");
    }

    @Test
    void testCreateMaterials_InvalidMaterial_NullDto() {
        List<MaterialDto> dtos = Collections.singletonList((MaterialDto) null);
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> materialService.createMaterials(dtos));
        assertEquals("Material data must not be null", ex.getMessage());
    }

    @Test
    void testCreateMaterials_InvalidMaterial_NullName() {
        MaterialDto dto = new MaterialDto();
        dto.setName(null);
        dto.setCost(10L);
        dto.setThermalInsulation("Good");
        List<MaterialDto> dtos = List.of(dto);
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> materialService.createMaterials(dtos));
        assertEquals("Material name must not be null or empty", ex.getMessage());
    }

    @Test
    void testCreateMaterials_InvalidMaterial_EmptyThermalInsulation() {
        MaterialDto dto = new MaterialDto();
        dto.setName("Material A");
        dto.setCost(10L);
        dto.setThermalInsulation("");
        List<MaterialDto> dtos = List.of(dto);
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> materialService.createMaterials(dtos));
        assertEquals("Material type of thermal insulation must not be null or empty", ex.getMessage());
    }

    @Test
    void testCreateMaterials_ValidList() {
        // Prepare two valid MaterialDto objects
        MaterialDto dto1 = new MaterialDto();
        dto1.setName("Material A");
        dto1.setCost(10L);
        dto1.setThermalInsulation("Good");

        MaterialDto dto2 = new MaterialDto();
        dto2.setName("Material B");
        dto2.setCost(20L);
        dto2.setThermalInsulation("Excellent");

        List<MaterialDto> dtos = Arrays.asList(dto1, dto2);

        // Create corresponding Material entities (simulate the mapping result)
        Material material1 = new Material();
        material1.setName(dto1.getName());
        material1.setCost(dto1.getCost());
        material1.setThermalInsulation(dto1.getThermalInsulation());
        Material material2 = new Material();
        material2.setName(dto2.getName());
        material2.setCost(dto2.getCost());
        material2.setThermalInsulation(dto2.getThermalInsulation());

        // Prepare what should be returned after mapping back to DTOs.
        MaterialDto savedDto1 = new MaterialDto();
        savedDto1.setName(material1.getName());
        savedDto1.setCost(material1.getCost());
        savedDto1.setThermalInsulation(material1.getThermalInsulation());
        MaterialDto savedDto2 = new MaterialDto();
        savedDto2.setName(material2.getName());
        savedDto2.setCost(material2.getCost());
        savedDto2.setThermalInsulation(material2.getThermalInsulation());

        // Use Mockito static mocking for MaterialMapper
        try (MockedStatic<MaterialMapper> mockedMapper = mockStatic(MaterialMapper.class)) {
            // Configure the mapper: from DTO to entity.
            mockedMapper.when(() -> MaterialMapper.toEntity(dto1)).thenReturn(material1);
            mockedMapper.when(() -> MaterialMapper.toEntity(dto2)).thenReturn(material2);
            // Configure the mapper: from entity to DTO.
            mockedMapper.when(() -> MaterialMapper.toDto(material1)).thenReturn(savedDto1);
            mockedMapper.when(() -> MaterialMapper.toDto(material2)).thenReturn(savedDto2);

            // Mock the repository’s bulk operation.
            when(materialRepository.saveAll(anyList()))
                    .thenReturn(Arrays.asList(material1, material2));

            // Execute the bulk creation.
            List<MaterialDto> result = materialService.createMaterials(dtos);

            // Validate that the results are as expected.
            assertEquals(2, result.size(), "Result list must contain two items");
            assertEquals(savedDto1.getName(), result.get(0).getName(), "First material name should match");
            assertEquals(savedDto2.getName(), result.get(1).getName(), "Second material name should match");

            // Verify that the repository was called exactly once.
            verify(materialRepository, times(1)).saveAll(anyList());
        }
    }
}
