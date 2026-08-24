package com.example.DigitalLibraryManagementSystem.service;

import com.example.DigitalLibraryManagementSystem.entity.Category;
import com.example.DigitalLibraryManagementSystem.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category addCategory(Category category) {

        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new RuntimeException("Category already exists");
        }

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));
    }

    public Category updateCategory(Long id, Category category) {

        Category existingCategory = getCategoryById(id);

        existingCategory.setName(category.getName());

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {

        Category category = getCategoryById(id);

        categoryRepository.delete(category);
    }
}