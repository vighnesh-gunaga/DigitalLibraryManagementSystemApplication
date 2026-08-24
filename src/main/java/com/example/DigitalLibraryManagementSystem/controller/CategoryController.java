package com.example.DigitalLibraryManagementSystem.controller;

import com.example.DigitalLibraryManagementSystem.entity.Category;
import com.example.DigitalLibraryManagementSystem.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Get all categories
    @GetMapping("/getallcategory")
    public List<Category> getAllCategory() {
        return categoryService.getAllCategories();
    }

    // Get category by ID
    @GetMapping("/getcategory/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // Add category
    @PostMapping("/addcategory")
    public Category addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    // Update category
    @PutMapping("/updatecategory/{id}")
    public Category updateCategory(
            @PathVariable Long id,
            @RequestBody Category category) {

        return categoryService.updateCategory(id, category);
    }

    // Delete category
    @DeleteMapping("/deletecategory/{id}")
    public String deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);

        return "Category deleted successfully";
    }
}