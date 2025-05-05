package com.example.petproject.service;

import com.example.petproject.dto.FurnaceDto;
import com.example.petproject.mappers.FurnaceMapper;
import com.example.petproject.model.Furnace;
import com.example.petproject.model.Project;
import com.example.petproject.repository.FurnaceRepository;
import com.example.petproject.repository.ProjectRepository;
import com.example.petproject.utils.BadRequestException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor
public class FurnaceService {
    private FurnaceRepository furnaceRepository;

    private ProjectRepository projectRepository;

    public List<FurnaceDto> getAllFurnaces() {
        return furnaceRepository.findAll().stream()
                .map(FurnaceMapper::toDto)
                .toList();
    }

    public FurnaceDto getFurnaceById(Long id) {
        if (id == null) {
            throw new BadRequestException("Furnace id must not be null");
        }
        return furnaceRepository.findById(id)
                .map(FurnaceMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Furnace not found with id " + id));
    }

    public FurnaceDto createFurnace(FurnaceDto furnaceDto) {
        if (furnaceDto == null) {
            throw new BadRequestException("Furnace data must not be null");
        }
        if (furnaceDto.getType() == null || furnaceDto.getType().isEmpty()) {
            throw new BadRequestException("Furnace type must not be null or empty");
        }
        Furnace furnace = FurnaceMapper.toEntity(furnaceDto);
        Furnace savedFurnace = furnaceRepository.save(furnace);
        return FurnaceMapper.toDto(savedFurnace);
    }

    public FurnaceDto updateFurnace(Long id, FurnaceDto furnaceDto) {
        if (id == null) {
            throw new BadRequestException("Furnace id must not be null");
        }
        if (furnaceDto == null) {
            throw new BadRequestException("Furnace data must not be null");
        }
        if (furnaceDto.getType() == null || furnaceDto.getType().isEmpty()) {
            throw new BadRequestException("Furnace type must not be null or empty");
        }
        return furnaceRepository.findById(id).map(furnace -> {
            furnace.setType(furnaceDto.getType());
            if (furnaceDto.getProjectId() != null) {
                Project project = projectRepository.findById(furnaceDto.getProjectId())
                        .orElseThrow(() -> new RuntimeException("Project not found with id " +
                                furnaceDto.getProjectId()));
                furnace.setProject(project);
            }
            Furnace updatedFurnace = furnaceRepository.save(furnace);
            return FurnaceMapper.toDto(updatedFurnace);
        }).orElseThrow(() -> new RuntimeException("Furnace not found with id " + id));
    }

    public void deleteFurnace(Long id) {
        if (id == null) {
            throw new BadRequestException("Furnace id must not be null");
        }
        furnaceRepository.deleteById(id);
    }
}
