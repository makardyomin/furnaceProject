package com.example.petproject.mappers;

import com.example.petproject.dto.FurnaceDto;
import com.example.petproject.model.Furnace;

public class FurnaceMapper {
    private FurnaceMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static FurnaceDto toDto(Furnace furnace) {
        FurnaceDto dto = new FurnaceDto();
        dto.setId(furnace.getId());
        dto.setType(furnace.getType());
        dto.setProjectId(furnace.getProject() != null ? furnace.getProject().getId() : null);
        dto.setMaterialIds(furnace.getMaterials() != null ? furnace.getMaterials()
                : null);
        return dto;
    }

    public static Furnace toEntity(FurnaceDto dto) {
        Furnace furnace = new Furnace();
        furnace.setId(dto.getId());
        furnace.setType(dto.getType());
        furnace.setMaterials(dto.getMaterialIds());
        return furnace;
    }
}
