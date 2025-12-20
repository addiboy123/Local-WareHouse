package com.Local_WareHouse.backend.repository;

import com.Local_WareHouse.backend.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
    Optional<Folder> findByIdAndOwnerId(UUID id, UUID ownerId);
    List<Folder> findByParentIdAndOwnerId(UUID parentId, UUID ownerId);
    List<Folder> findByOwnerIdAndParentIsNull(UUID ownerId);
}
