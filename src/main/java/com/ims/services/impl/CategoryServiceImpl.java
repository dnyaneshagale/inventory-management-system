package com.ims.services.impl;

import com.ims.dto.CategoryDto;
import com.ims.entities.Category;
import com.ims.exceptions.ResourceNotFoundException;
import com.ims.repositories.CategoryRepository;
import com.ims.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = mapToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return mapToDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + categoryDto.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    // Helper methods for mapping between entity and DTO
    private Category mapToEntity(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + categoryDto.getParentId()));
            category.setParent(parent);
        }

        return category;
    }

    private CategoryDto mapToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());

        if (category.getParent() != null) {
            categoryDto.setParentId(category.getParent().getId());
            categoryDto.setParentName(category.getParent().getName());
        }

        // Map subcategories if any
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            categoryDto.setSubcategories(category.getSubcategories().stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList()));
        }

        // Set product count
        if (category.getProducts() != null) {
            categoryDto.setProductCount(category.getProducts().size());
        } else {
            categoryDto.setProductCount(0);
        }

        return categoryDto;
    }
}