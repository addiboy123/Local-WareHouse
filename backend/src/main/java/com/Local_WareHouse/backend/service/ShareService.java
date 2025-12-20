package com.Local_WareHouse.backend.service;

import com.Local_WareHouse.backend.dto.*;
import com.Local_WareHouse.backend.entity.*;
import com.Local_WareHouse.backend.repository.*;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
@Service
public class ShareService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final FileShareRepository fileShareRepo;
    private final FolderShareRepository folderShareRepo;
    private final UserRepository userRepository;

    public ShareService(
        FileRepository fileRepository,
        FolderRepository folderRepository,
        FileShareRepository fileShareRepo,
        FolderShareRepository folderShareRepo,
        UserRepository userRepository
    ) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.fileShareRepo = fileShareRepo;
        this.folderShareRepo = folderShareRepo;
        this.userRepository = userRepository;
    }

    public void shareFile(ShareRequest req, UUID ownerId) {

        FileMetadata file = fileRepository
            .findById(req.getResourceId())
            .filter(f -> f.getOwner().getId().equals(ownerId))
            .orElseThrow(() -> new RuntimeException("File not found"));

        User owner = userRepository.getReferenceById(ownerId);

        for (var entry : req.getUsers().entrySet()) {

            java.util.UUID sharedUserId = entry.getKey();
            Permission permission = entry.getValue();

            User target = userRepository.getReferenceById(sharedUserId);

            FileShare share = new FileShare();
            share.setFile(file);
            share.setSharedBy(owner);
            share.setSharedWith(target);
            share.setPermission(permission);

            fileShareRepo.save(share);
        }
    }


    public void shareFolder(ShareRequest req, UUID ownerId) {

        Folder folder = folderRepository
            .findById(req.getResourceId())
            .filter(f -> f.getOwner().getId().equals(ownerId))
            .orElseThrow(() -> new RuntimeException("Folder not found"));

        User owner = userRepository.getReferenceById(ownerId);

        for (var entry : req.getUsers().entrySet()) {

            java.util.UUID sharedUserId = entry.getKey();
            Permission permission = entry.getValue();

            User target = userRepository.getReferenceById(sharedUserId);

            FolderShare share = new FolderShare();
            share.setFolder(folder);
            share.setSharedBy(owner);
            share.setSharedWith(target);
            share.setPermission(permission);

            folderShareRepo.save(share);
        }
    }


    public Page<FileMetadata> getSharedFiles(UUID userId, int page, int size) {
        return fileShareRepo
            .findBySharedWithId(
                userId,
                PageRequest.of(page, size)
            )
            .map(FileShare::getFile);
    }

    public Page<Folder> getSharedFolders(UUID userId, int page, int size) {
        return folderShareRepo
            .findBySharedWithId(
                userId,
                PageRequest.of(page, size)
            )
            .map(FolderShare::getFolder);
    }
}

