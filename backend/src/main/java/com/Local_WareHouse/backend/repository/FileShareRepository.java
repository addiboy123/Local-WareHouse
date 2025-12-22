package com.Local_WareHouse.backend.repository;

import com.Local_WareHouse.backend.entity.FileShare;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileShareRepository extends JpaRepository<FileShare, UUID> {
    Page<FileShare> findBySharedWithId(UUID userId, Pageable pageable);
    Optional<FileShare> findByFileIdAndSharedWithId(UUID fileId, UUID userId);
    void deleteByFileIdAndSharedWithId(UUID fileId, UUID userId);


}
