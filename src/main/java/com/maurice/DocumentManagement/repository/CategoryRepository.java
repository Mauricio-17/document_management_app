package com.maurice.DocumentManagement.repository;

import com.maurice.DocumentManagement.entities.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT cat FROM Category cat JOIN cat.documents doc WHERE doc.documentDetail.id IN (SELECT det.id FROM DocumentDetail det WHERE det.folder.id IN (SELECT fol.id FROM Folder fol WHERE fol.user.id = :userId))")
    Optional<List<Category>> findCategoriesByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Category cat SET cat.name = :name , cat.description = :description , cat.lastModifiedAt = :lastModifiedAt WHERE cat.id = :categoryId")
    int updateCategory(@Param("name") String name, @Param("description") String description, @Param("lastModifiedAt") LocalDateTime lastModifiedAt, @Param("categoryId") Long categoryId);


}
