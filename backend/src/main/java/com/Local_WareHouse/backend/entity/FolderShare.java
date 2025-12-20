package com.Local_WareHouse.backend.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "folder_shares",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"folder_id", "shared_with_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class FolderShare {
    @Id
    @GeneratedValue
    private UUID id;

    // Folder being shared
    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    // Owner who shared it
    @ManyToOne
    @JoinColumn(name = "shared_by_id", nullable = false)
    private User sharedBy;

    // User who receives access
    @ManyToOne
    @JoinColumn(name = "shared_with_id", nullable = false)
    private User sharedWith;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Permission permission;

    private Instant sharedAt = Instant.now();
}
