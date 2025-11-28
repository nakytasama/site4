package org.example.site4.controller;

import lombok.RequiredArgsConstructor;
import org.example.site4.domain.Category;
import org.example.site4.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // Получить все категории
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // Получить категорию по ID
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
    }

    // Получить категорию по имени
    @GetMapping("/name/{name}")
    public Category getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
    }
}