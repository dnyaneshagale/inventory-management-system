package com.ims.inventory_management_system.services;

import com.ims.inventory_management_system.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto getCategoryById(Long id);
    List<CategoryDto> getAllCategories();
    List<CategoryDto> getRootCategories();
    List<CategoryDto> getSubcategories(Long parentId);
    CategoryDto updateCategory(Long id, CategoryDto categoryDto);
    void deleteCategory(Long id);
}