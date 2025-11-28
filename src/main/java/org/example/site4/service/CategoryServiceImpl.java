package org.example.site4.service;

import lombok.RequiredArgsConstructor;
import org.example.site4.domain.Category;
import org.example.site4.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Category saveCategory(Category category) {
        // Проверка существования категории с таким названием
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Категория с именем '" + category.getName() + "' уже существует");
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(category -> {
                    // Проверка существования категории с таким названием (при редактировании)
                    if (!category.getName().equals(categoryDetails.getName()) &&
                            categoryRepository.existsByName(categoryDetails.getName())) {
                        throw new RuntimeException("Категория с именем '" + categoryDetails.getName() + "' уже существует");
                    }

                    category.setName(categoryDetails.getName());
                    category.setDescription(categoryDetails.getDescription());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
    }

    @Override
    public void deleteCategory(Long id) {
        // Проверка заполнена ли категория
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        if (!category.getImages().isEmpty()) {
            throw new RuntimeException("Нельзя удалить категорию, так как с ней связаны изображения");
        }

        categoryRepository.delete(category);
    }
}