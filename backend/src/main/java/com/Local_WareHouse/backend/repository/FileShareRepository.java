package com.Local_WareHouse.backend.repository;

import com.Local_WareHouse.backend.entity.FileShare;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileShareRepository extends JpaRepository<FileShare, UUID> {
    Page<FileShare> findBySharedWithId(UUID userId, Pageable pageable);
}
