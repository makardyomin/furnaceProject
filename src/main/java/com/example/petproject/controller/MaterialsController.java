package com.example.petproject.controller;

import com.example.petproject.dto.MaterialDto;
import com.example.petproject.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/materials")
@Tag(name = "Материалы", description = "Управление материалами")
@AllArgsConstructor
public class MaterialsController {
    private MaterialService materialService;

    @Operation(summary = "Получить список всех материалов")
    @GetMapping
    public List<MaterialDto> getAllMaterials() {
        return materialService.getAllMaterials();
    }

    @Operation(summary = "Получить материал по id")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialDto> getMaterialById(@PathVariable Long id) {
        MaterialDto material = materialService.getMaterialById(id);
        return ResponseEntity.ok(material);
    }

    @Operation(summary = "Получить список материалов по типу конструкции печи")
    @GetMapping("/search")
    public List<MaterialDto> getByFurnaceType(@RequestParam String type) {
        return materialService.findByFurnaceType(type);
    }

    @Operation(summary = "Создать новый материал")
    @PostMapping
    public MaterialDto createMaterial(@RequestBody MaterialDto materialDto) {
        return materialService.createMaterial(materialDto);
    }

    @Operation(summary = "Создать список материалов (bulk-операция)")
    @PostMapping("/bulk")
    public List<MaterialDto> createMaterials(@RequestBody List<MaterialDto> materialDtos) {
        return materialDtos.stream()
                .map(materialService::createMaterial)
                .toList();
    }

    @Operation(summary = "Обновить материал по id")
    @PutMapping("/{id}")
    public MaterialDto updateMaterial(@PathVariable Long id, @RequestBody MaterialDto materialDto) {
        return materialService.updateMaterial(id, materialDto);
    }

    @Operation(summary = "Удалить материал по id")
    @DeleteMapping("/{id}")
    public void deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
    }
}
