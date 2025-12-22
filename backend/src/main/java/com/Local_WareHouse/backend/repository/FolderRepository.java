package com.Local_WareHouse.backend.repository;

import com.Local_WareHouse.backend.entity.Folder;
import com.Local_WareHouse.backend.entity.FolderShare;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
    Optional<Folder> findByIdAndOwnerId(UUID id, UUID ownerId);
    Page<Folder> findByParentIdAndOwnerId(UUID parentId, UUID ownerId, Pageable pageable);
    Page<Folder> findByOwnerIdAndParentIsNull(UUID ownerId, Pageable pageable);
    Optional<FolderShare> findByFolderIdAndSharedWithId(UUID folderId, UUID userId);
    Page<Folder> findByParentIsNullAndOwnerId(UUID ownerId, Pageable pageable);
    void deleteByFolderIdAndSharedWithId(UUID folderId, UUID userId);
    Page<Folder> findByNameContainingIgnoreCaseAndOwnerId(String name, UUID ownerId, Pageable pageable);
    List<Folder> findByParentId(UUID parentId);

}
