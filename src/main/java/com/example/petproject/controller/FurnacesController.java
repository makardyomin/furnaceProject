package com.example.petproject.controller;

import com.example.petproject.dto.FurnaceDto;
import com.example.petproject.service.FurnaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/furnaces")
@Tag(name = "Печи", description = "Управление печями")
@AllArgsConstructor
public class FurnacesController {
    private FurnaceService furnaceService;

    @Operation(summary = "Получить список всех печей")
    @GetMapping
    public List<FurnaceDto> getAllFurnaces() {
        return furnaceService.getAllFurnaces();
    }

    @Operation(summary = "Получить печь по id")
    @GetMapping("/{id}")
    public FurnaceDto getFurnaceById(@PathVariable Long id) {
        return furnaceService.getFurnaceById(id);
    }

    @Operation(summary = "Создать новую печь")
    @PostMapping
    public FurnaceDto createFurnace(@RequestBody FurnaceDto furnaceDto) {
        return furnaceService.createFurnace(furnaceDto);
    }

    @Operation(summary = "Обновить печь по id")
    @PutMapping("/{id}")
    public FurnaceDto updateFurnace(@PathVariable Long id, @RequestBody FurnaceDto furnaceDto) {
        return furnaceService.updateFurnace(id, furnaceDto);
    }

    @Operation(summary = "Удалить печь по id")
    @DeleteMapping("/{id}")
    public void deleteFurnace(@PathVariable Long id) {
        furnaceService.deleteFurnace(id);
    }
}
