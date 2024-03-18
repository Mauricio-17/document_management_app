package com.maurice.DocumentManagement.services;

import com.maurice.DocumentManagement.dto.CategoryDto;
import com.maurice.DocumentManagement.entities.Category;
import com.maurice.DocumentManagement.entities.Document;
import com.maurice.DocumentManagement.exceptions.BadRequestException;
import com.maurice.DocumentManagement.exceptions.CreateStatusException;
import com.maurice.DocumentManagement.exceptions.NotFoundException;
import com.maurice.DocumentManagement.repository.CategoryRepository;
import com.maurice.DocumentManagement.repository.DocumentRepository;
import com.maurice.DocumentManagement.utils.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DocumentRepository documentRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, DocumentRepository documentRepository) {
        this.categoryRepository = categoryRepository;
        this.documentRepository = documentRepository;
    }

    public List<CategoryDto> getCategoriesByUser(Long userId) {
        return categoryRepository.findCategoriesByUserId(userId).orElseThrow(
                () -> CreateStatusException.getThrowableException("Categories not found.", 404)
        ).stream().map(Mappers.categoryToDto).toList();
    }

    public void insertCategory(CategoryDto category, Long userId) {
        Optional<List<Category>> found = categoryRepository.findCategoriesByUserId(userId);
        found.ifPresent(categories -> categories.forEach(item -> {
            if (item.getName().equalsIgnoreCase(category.name())) {
                throw CreateStatusException.getThrowableException("Name "+ item.getName() +" already exists.", 400);
            }
        }));
        Category cat = Mappers.dtoToCategory.apply(category);
        cat.setLastModifiedAt(LocalDateTime.now());
        cat.setCreatedAt(LocalDateTime.now());
        categoryRepository.save(cat);
    }

    public void updateCategory(CategoryDto category, Long id) {
        int result = categoryRepository.updateCategory(category.name(), category.description(), LocalDateTime.now(), id);
        if (result == 0) {
            throw CreateStatusException.getThrowableException("Category not found to be updated.", 400);
        }
    }

    public void deleteCategory(Long categoryId) {
        Optional<Category> found = categoryRepository.findById(categoryId);
        if (found.isEmpty()) {
            throw new NotFoundException("Category with ID:" + categoryId + " not found to be deleted");
        }
        categoryRepository.delete(found.get());
    }

    public void addDocument(Long documentId, Long categoryId) {
        Document foundDoc = documentRepository.findById(documentId).orElseThrow(
                () -> CreateStatusException.getThrowableException("Document not found.", 404)
        );
        Category foundCat = categoryRepository.findById(categoryId).orElseThrow(
                () -> CreateStatusException.getThrowableException("Document not found.", 404)
        );

        List<Document> listOfDocuments = foundCat.getDocuments();

        listOfDocuments.add(foundDoc);

        foundCat.setDocuments(listOfDocuments);

        // Updating the category
        categoryRepository.save(foundCat);
    }

    public void removeDocument(Long documentId, Long categoryId) {
        Document foundDoc = documentRepository.findById(documentId).orElseThrow(
                () -> CreateStatusException.getThrowableException("Document not found.", 404)
        );
        Category foundCat = categoryRepository.findById(categoryId).orElseThrow(
                () -> CreateStatusException.getThrowableException("Document not found.", 404)
        );

        List<Document> listOfDocuments = foundCat.getDocuments();

        foundCat.setDocuments(
                listOfDocuments.stream().filter(item -> !Objects.equals(item.getId(), foundDoc.getId())).collect(Collectors.toList())
        );

        // Updating the category
        categoryRepository.save(foundCat);
    }
}
