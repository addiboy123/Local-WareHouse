package com.Local_WareHouse.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Local_WareHouse.backend.entity.FileMetadata;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileMetadata, UUID> {

    Optional<FileMetadata> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<FileMetadata> findByFolderIdAndOwnerId(UUID folderId, UUID ownerId);

    List<FileMetadata> findByFolderIsNullAndOwnerId(UUID ownerId);
}
