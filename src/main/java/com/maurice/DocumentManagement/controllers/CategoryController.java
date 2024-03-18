package com.maurice.DocumentManagement.controllers;

import com.maurice.DocumentManagement.dto.CategoryDto;
import com.maurice.DocumentManagement.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping("/category/{id}")
    public List<CategoryDto> getCategoriesByUser(@PathVariable("id") Long userId) {
        return categoryService.getCategoriesByUser(userId);
    }

    @PostMapping("/user/{id}")
    public ResponseEntity<?> registerCategory(@PathVariable("id") Long userId, @RequestBody CategoryDto request) {
        categoryService.insertCategory(request, userId);
        return ResponseEntity.status(201).body(null);
    }

    @PostMapping("/document/{id}")
    public ResponseEntity<?> addDocumentToCategory(
            @PathVariable("id") Long documentId,
            @RequestParam("categoryId") Long categoryId
    ){
        categoryService.addDocument(documentId, categoryId);
        return ResponseEntity.status(201).body(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Long categoryId, @RequestBody CategoryDto payload) {
        categoryService.updateCategory(payload, categoryId);
        return ResponseEntity.status(204).body(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long categoryId){
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(204).body(null);
    }

    @DeleteMapping("/document/{id}")
    public ResponseEntity<?> removeDocumentFromCategory(
            @PathVariable("id") Long documentId,
            @RequestParam("categoryId") Long categoryId
    ){
        categoryService.removeDocument(documentId, categoryId);
        return ResponseEntity.status(204).body(null);
    }
}

