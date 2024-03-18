package com.maurice.DocumentManagement.repository;

import com.maurice.DocumentManagement.entities.Document;
import com.maurice.DocumentManagement.entities.DocumentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentDetailRepository extends JpaRepository<DocumentDetail, Long> {

    @Query("SELECT dt FROM DocumentDetail dt WHERE dt.document.key IN (:keys)")
    List<DocumentDetail> findDocumentDetailsByDocumentKey(@Param("keys") List<String> key);

    @Query("SELECT dt FROM DocumentDetail dt WHERE dt.share.id = :shareId")
    List<DocumentDetail> findDocumentDetailsByShareId(@Param("shareId") Long shareId);

}
