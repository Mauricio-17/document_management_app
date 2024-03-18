package com.maurice.DocumentManagement.repository;


import com.maurice.DocumentManagement.entities.Category;
import com.maurice.DocumentManagement.entities.Document;
import com.maurice.DocumentManagement.entities.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT doc FROM Document doc WHERE doc.isPublic = true")
    Page<Document> findDocumentsByIsPublic(Pageable pageable);

    @Query("SELECT doc FROM Document doc WHERE doc.documentDetail.id IN" +
            "(SELECT det.id FROM DocumentDetail det WHERE det.folder.id IN" +
            "(SELECT fol.id FROM Folder fol WHERE fol.user.id = :userId))")
    Page<Document> findDocumentsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT doc FROM Document doc WHERE doc.documentDetail.id IN" +
            "(SELECT det.id FROM DocumentDetail det WHERE det.share.id IN" +
            "(SELECT sh.id FROM Share sh WHERE sh.id = :shareId))")
    List<Document> findDocumentsByShareId(@Param("shareId") Long shareId);

    @Query("SELECT doc FROM Document doc JOIN doc.categories cat WHERE cat.id = :categoryId")
    List<Document> findDocumentsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT doc FROM Document doc WHERE doc.documentDetail IN (SELECT det FROM DocumentDetail det WHERE det.folder.id = :folderId)")
    Page<Document> findDocumentsByFolderId(@Param("folderId") Long folderId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Document doc SET doc.fileName = :fileName, doc.status = :status , doc.isPublic = :isPublic, doc.key = :key WHERE doc.id = :documentId")
    int updateDocument(
            @Param("fileName") String fileName,
            @Param("status") Status status,
            @Param("isPublic") boolean isPublic,
            @Param("key") String key,
            @Param("documentId") Long documentId);

    @Query("SELECT doc FROM Document doc WHERE doc.key = :key")
    Optional<Document> findDocumentByKey(@Param("key") String key);

}
