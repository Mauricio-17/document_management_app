package com.maurice.DocumentManagement.repository;

import com.maurice.DocumentManagement.entities.Folder;
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
public interface FolderRepository extends JpaRepository<Folder, Long> {

    @Query("SELECT fol FROM Folder fol WHERE fol.user.id = :userId")
    Optional<List<Folder>> findFoldersByUserId(@Param("userId") Long userId);

    @Query("SELECT fol FROM Folder fol WHERE fol.key LIKE :folderKey%")
    List<Folder> findFoldersByParentFolderKey(@Param("folderKey") String folderKey);

    @Query("SELECT fol FROM Folder fol WHERE fol.key = :folderKey")
    Optional<Folder> findFolderByFolderKey(@Param("folderKey") String folderKey);

    @Modifying
    @Transactional
    @Query("UPDATE Folder fol SET fol.name = :name , fol.description = :description, fol.lastModifiedAt = :lastModifiedAt WHERE fol.id = :folderId")
    int updateFolder(@Param("name") String name, @Param("description") String description, @Param("lastModifiedAt") LocalDateTime lastModifiedAt, @Param("folderId") Long folderId);

    @Query("SELECT fol FROM Folder fol WHERE fol.key = :key")
    Optional<Folder> findFolderByKey(@Param("key") String key);

    @Query("SELECT fol FROM Folder fol WHERE fol.key LIKE :key%")
    List<Folder> findFoldersByFolderKey(@Param("key") String key);
}
