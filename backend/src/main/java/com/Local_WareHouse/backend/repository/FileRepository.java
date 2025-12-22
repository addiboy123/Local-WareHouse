package com.Local_WareHouse.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.Local_WareHouse.backend.entity.FileMetadata;
import com.Local_WareHouse.backend.entity.Folder;

import java.util.Optional;
import java.io.File;
import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileMetadata, UUID> {
    Page<FileMetadata> findByFolderIsNullAndOwnerId(UUID ownerId, Pageable pageable);
    Optional<FileMetadata> findByIdAndOwnerId(UUID id, UUID ownerId);

    Page<FileMetadata> findByFolderIdAndOwnerId(UUID folderId, UUID ownerId, Pageable pageable);

    Page<FileMetadata> findByOriginalNameContainingIgnoreCaseAndOwnerId(String name, UUID ownerId, Pageable pageable);

    List<FileMetadata> findByFolderIsNullAndOwnerId(UUID ownerId);
    List<FileMetadata> findByFolderId(UUID folderId);
}
