package com.example.petproject.service;

import com.example.petproject.cache.Cache;
import com.example.petproject.dto.MaterialDto;
import com.example.petproject.mappers.MaterialMapper;
import com.example.petproject.model.Material;
import com.example.petproject.repository.MaterialRepository;
import com.example.petproject.utils.BadRequestException;
import com.example.petproject.utils.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final Cache<List<Material>> cache;
    private final Logger logger = LoggerFactory.getLogger(MaterialService.class);
    String exceptionMessage = "Material not found with id";

    public MaterialService(MaterialRepository materialRepository,
                           Cache<List<Material>> cache) {
        this.materialRepository = materialRepository;
        this.cache = cache;
    }

    public List<MaterialDto> getAllMaterials() {
        logger.info("Finding all materials");
        return materialRepository.findAll().stream()
                .map(MaterialMapper::toDto)
                .toList();
    }

    public MaterialDto getMaterialById(Long id) {
        if (id == null) {
            throw new BadRequestException("Material id must not be null");
        }
        logger.info("Finding material by id");
        return materialRepository.findById(id)
                .map(MaterialMapper::toDto)
                .orElseThrow(() -> new NotFoundException(exceptionMessage + id));
    }

    public List<MaterialDto> findByFurnaceType(String type) {
        if (type == null || type.isEmpty()) {
            throw new BadRequestException("Необходимо указать параметр поиска!");
        }
        logger.info("Finding materials by criteria");
        List<Material> materials = cache.get(type);
        if (materials != null) {
            logger.info("Found cached materials");
            return materials.stream().map(MaterialDto::new).toList();
        }

        materials = materialRepository.findByFurnaceType(type);
        if (materials == null || materials.isEmpty()) {
            throw new NotFoundException("Material not found with type " + type);
        }

        for (Material material : materials) {
            cache.trackKey(material.getId(), type);
        }
        cache.put(type, materials);

        logger.info("Found materials from database, saved in cache");
        return materials.stream().map(MaterialDto::new).toList();
    }

    public MaterialDto createMaterial(MaterialDto materialDto) {
        if (materialDto == null) {
            throw new BadRequestException("Material data must not be null");
        }
        if (materialDto.getName() == null || materialDto.getName().isEmpty()) {
            throw new BadRequestException("Material name must not be null or empty");
        }
        if (materialDto.getCost() == null) {
            throw new BadRequestException("Material cost must not be null");
        }
        if (materialDto.getThermalInsulation() == null || materialDto.getThermalInsulation().isEmpty()) {
            throw new BadRequestException("Material type of thermal insulation must not be null or empty");
        }
        logger.info("Creating material");
        Material material = MaterialMapper.toEntity(materialDto);
        Material savedMaterial = materialRepository.save(material);
        return MaterialMapper.toDto(savedMaterial);
    }

    public MaterialDto updateMaterial(Long id, MaterialDto materialDto) {
        if (id == null) {
            throw new BadRequestException("Material id must not be null");
        }
        if (materialDto == null) {
            throw new BadRequestException("Material data must not be null");
        }
        if (materialDto.getName() == null || materialDto.getName().isEmpty()) {
            throw new BadRequestException("Material name must not be null or empty for update");
        }
        if (materialDto.getCost() == null) {
            throw new BadRequestException("Material cost must not be null");
        }
        if (materialDto.getThermalInsulation() == null || materialDto.getThermalInsulation().isEmpty()) {
            throw new BadRequestException("Material type of thermal insulation must not be null or empty");
        }
        logger.info("Updating material with id {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionMessage + id));

        material.setName(materialDto.getName());
        material.setThermalInsulation(materialDto.getThermalInsulation());
        Material updatedMaterial = materialRepository.save(material);

        Set<String> cacheKeys = cache.getKeys(id);
        Material oldMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionMessage + id));
        if (cacheKeys != null) {
            for (String cacheKey : cacheKeys) {
                List<Material> materials = cache.get(cacheKey);
                materials.remove(oldMaterial);
                materials.add(updatedMaterial);
                cache.put(cacheKey, materials);
            }
        }
        return MaterialMapper.toDto(updatedMaterial);
    }

    public void deleteMaterial(Long id) {
        if (id == null) {
            throw new BadRequestException("Material id must not be null");
        }
        logger.info("Deleting material with id {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(exceptionMessage + id));
        material.getFurnaces().forEach(furnace -> furnace.getMaterials().remove(material));
        materialRepository.deleteById(id);
        cache.remove(id);
    }

    public List<MaterialDto> createMaterials(List<MaterialDto> materialDtos) {
        if (materialDtos == null) {
            throw new BadRequestException("Material list must not be null");
        }
        if (materialDtos.isEmpty()) {
            return Collections.emptyList();
        }

        List<Material> materialsToSave = materialDtos.stream().map(materialDto -> {
            if (materialDto == null) {
                throw new BadRequestException("Material data must not be null");
            }
            if (materialDto.getName() == null || materialDto.getName().isEmpty()) {
                throw new BadRequestException("Material name must not be null or empty");
            }
            if (materialDto.getCost() == null) {
                throw new BadRequestException("Material cost must not be null");
            }
            if (materialDto.getThermalInsulation() == null || materialDto.getThermalInsulation().isEmpty()) {
                throw new BadRequestException(
                        "Material type of thermal insulation must not be null or empty");
            }
            return MaterialMapper.toEntity(materialDto);
        }).toList();

        logger.info("Creating {} materials in bulk", materialsToSave.size());

        // Bulk save using saveAll()
        List<Material> savedMaterials = materialRepository.saveAll(materialsToSave);

        return savedMaterials.stream()
                .map(MaterialMapper::toDto)
                .toList();
    }
}

