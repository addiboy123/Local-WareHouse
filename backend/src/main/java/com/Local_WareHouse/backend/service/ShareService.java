package com.Local_WareHouse.backend.service;

import com.Local_WareHouse.backend.dto.*;
import com.Local_WareHouse.backend.entity.*;
import com.Local_WareHouse.backend.exception.ForbiddenException;
import com.Local_WareHouse.backend.exception.ResourceNotFoundException;
import com.Local_WareHouse.backend.repository.*;

import jakarta.transaction.Transactional;

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


    // ------- FILE -------

    // ------ GET SHARED FILES --------
    public Page<FileMetadata> getSharedFiles(UUID userId, int page, int size) {
        return fileShareRepo
            .findBySharedWithId(
                userId,
                PageRequest.of(page, size)
            )
            .map(FileShare::getFile);
    }
    //   ------ SHARE FILE ------
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

    public void shareFileByEmail(ShareByEmailRequest req, UUID ownerId) {
        FileMetadata file = fileRepository.findById(req.getResourceId())
            .filter(item -> item.getOwner().getId().equals(ownerId))
            .orElseThrow(() -> new ResourceNotFoundException("File not found"));
        User target = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User with that email was not found"));
        if (target.getId().equals(ownerId)) {
            throw new ForbiddenException("A file cannot be shared with its owner");
        }
        FileShare share = fileShareRepo.findByFileIdAndSharedWithId(file.getId(), target.getId())
            .orElseGet(FileShare::new);
        share.setFile(file);
        share.setSharedBy(userRepository.getReferenceById(ownerId));
        share.setSharedWith(target);
        share.setPermission(req.getPermission());
        fileShareRepo.save(share);
    }

    // ------ UPDATE FILE SHARE ------  
    public void updateFileShare(UpdateShareRequest req, UUID ownerId) {

        FileMetadata file = fileRepository
            .findById(req.getResourceId())
            .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        if (!file.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can update shares");
        }

        FileShare share = fileShareRepo
            .findByFileIdAndSharedWithId(
                req.getResourceId(), req.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Share not found"));

        share.setPermission(req.getPermission());
        fileShareRepo.save(share);
    }

    // ------ REVOKE FILE SHARE ------
    public void revokeFileShare(RevokeShareRequest req, UUID ownerId) {

    FileMetadata file = fileRepository
        .findById(req.getResourceId())
        .orElseThrow(() -> new ResourceNotFoundException("File not found"));

    if (!file.getOwner().getId().equals(ownerId)) {
        throw new ForbiddenException("Only owner can revoke shares");
    }

    fileShareRepo.deleteByFileIdAndSharedWithId(
        req.getResourceId(), req.getUserId());
    }


    // ------ FOLDER ------

    // ------ GET SHARED FOLDERS --------
    public Page<Folder> getSharedFolders(UUID userId, int page, int size) {
        return folderShareRepo
            .findBySharedWithId(
                userId,
                PageRequest.of(page, size)
            )
            .map(FolderShare::getFolder);
    }

    //   ------ SHARE FOLDER ------
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
    //     ------ UPDATE FOLDER SHARE ------
    @Transactional
    public void updateFolderShare(UpdateShareRequest req, UUID ownerId) {

        Folder folder = folderRepository
            .findById(req.getResourceId())
            .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        if (!folder.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can update shares");
        }

        updateFolderShareRecursive(folder, req.getUserId(), req.getPermission());
    }
    private void updateFolderShareRecursive(
        Folder folder,
        UUID userId,
        Permission permission) {

        folderShareRepo
            .findByFolderIdAndSharedWithId(folder.getId(), userId)
            .ifPresent(share -> {
                share.setPermission(permission);
                folderShareRepo.save(share);
            });

        for (FileMetadata file : fileRepository.findByFolderId(folder.getId())) {
            fileShareRepo
                .findByFileIdAndSharedWithId(file.getId(), userId)
                .ifPresent(share -> {
                    share.setPermission(permission);
                    fileShareRepo.save(share);
                });
        }

        for (Folder child : folderRepository.findByParentId(folder.getId())) {
            updateFolderShareRecursive(child, userId, permission);
        }
    }

    // ------ REVOKE FOLDER SHARE ------

    @Transactional
    public void revokeFolderShare(RevokeShareRequest req, UUID ownerId) {

        Folder folder = folderRepository
            .findById(req.getResourceId())
            .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        if (!folder.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can revoke shares");
        }

        revokeFolderShareRecursive(folder, req.getUserId());
    }

    private void revokeFolderShareRecursive(
        Folder folder, UUID userId) {

        folderShareRepo.deleteByFolderIdAndSharedWithId(folder.getId(), userId);

        for (FileMetadata file : fileRepository.findByFolderId(folder.getId())) {
            fileShareRepo.deleteByFileIdAndSharedWithId(file.getId(), userId);
        }

        for (Folder child : folderRepository.findByParentId(folder.getId())) {
            revokeFolderShareRecursive(child, userId);
        }
    }



    

}

