package com.example.petproject.mappers;

import com.example.petproject.dto.MaterialDto;
import com.example.petproject.model.Furnace;
import com.example.petproject.model.Material;
import java.util.stream.Collectors;

public class MaterialMapper {
    private MaterialMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static MaterialDto toDto(Material material) {
        MaterialDto dto = new MaterialDto();
        dto.setId(material.getId());
        dto.setName(material.getName());
        dto.setThermalInsulation(material.getThermalInsulation());
        dto.setCost(material.getCost());
        dto.setFurnaceIds(material.getFurnaces() != null ? material.getFurnaces()
                .stream()
                .map(Furnace::getId) // Map each Furnace to its ID
                .collect(Collectors.toList())
                : null); // Handle null case
        return dto;
    }

    public static Material toEntity(MaterialDto dto) {
        Material material = new Material();
        material.setId(dto.getId());
        material.setName(dto.getName());
        material.setThermalInsulation(dto.getThermalInsulation());
        material.setCost(dto.getCost());
        return material; // Note: Furnaces need to be set separately in the service
    }
}
