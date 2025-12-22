package com.Local_WareHouse.backend.repository;

import com.Local_WareHouse.backend.entity.FolderShare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FolderShareRepository extends JpaRepository<FolderShare, UUID> {

    // Paging: folders shared WITH a user
    Page<FolderShare> findBySharedWithId(UUID userId, Pageable pageable);

    // Lookup specific share (for update permission)
    Optional<FolderShare> findByFolderIdAndSharedWithId(
            UUID folderId,
            UUID userId
    );

    // Revoke access
    void deleteByFolderIdAndSharedWithId(
            UUID folderId,
            UUID userId
    );
}
