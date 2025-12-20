package com.Local_WareHouse.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Local_WareHouse.backend.entity.FolderShare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface FolderShareRepository extends JpaRepository<FolderShare, UUID> {
    Page<FolderShare> findBySharedWithId(UUID userId, Pageable pageable);

}



