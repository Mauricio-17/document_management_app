package com.maurice.DocumentManagement.repository;

import com.maurice.DocumentManagement.dto.ShareUser;
import com.maurice.DocumentManagement.entities.PermissionAsset;
import com.maurice.DocumentManagement.entities.Share;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {

    @Query("SELECT sh FROM Share sh WHERE sh.id IN (SELECT det.share.id FROM DocumentDetail det WHERE det.folder.id IN (SELECT fol.id FROM Folder fol WHERE fol.user.id = :userId))")
    List<Share> findSharesByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Share sh SET sh.permission = :permissionAsset WHERE sh.id = :shareId")
    int updateShare(@Param("permissionAsset") PermissionAsset permissionAsset, @Param("shareId") Long shareId);

}
