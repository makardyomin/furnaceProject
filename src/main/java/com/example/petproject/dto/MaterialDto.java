package com.example.petproject.dto;

import com.example.petproject.model.Furnace;
import com.example.petproject.model.Material;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDto {
    private Long id;

    private String name;
    private List<Long> furnaceIds;
    private String thermalInsulation;
    private Long cost;

    // Constructor that converts Material to MaterialDTO
    public MaterialDto(Material material) {
        this.id = material.getId();
        this.name = material.getName();
        this.thermalInsulation = material.getThermalInsulation();
        this.cost = material.getCost();
        // Extract furnace IDs instead of full Furnace objects
        this.furnaceIds = material.getFurnaces()
                .stream()
                .map(Furnace::getId) // Get only the ID of each Furnace
                .collect(Collectors.toList());
    }
}
