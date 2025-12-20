package com.Local_WareHouse.backend.service;

import com.Local_WareHouse.backend.dto.*;
import com.Local_WareHouse.backend.entity.Folder;
import com.Local_WareHouse.backend.entity.FileMetadata;
import com.Local_WareHouse.backend.entity.User;
import com.Local_WareHouse.backend.repository.FolderRepository;
import com.Local_WareHouse.backend.repository.FileRepository;
import com.Local_WareHouse.backend.repository.UserRepository;


import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FolderService(
            FolderRepository folderRepository,
            FileRepository fileRepository,
            UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    // -------- CREATE --------
    public Folder create(CreateFolderRequest request, UUID userId) {

        User owner = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Folder folder = new Folder();
        folder.setName(request.getName());
        folder.setOwner(owner);

        if (request.getParentId() != null) {
            Folder parent = folderRepository
                .findByIdAndOwnerId(request.getParentId(), userId)
                .orElseThrow(() -> new RuntimeException("Parent folder not found"));

            folder.setParent(parent);
        }

        return folderRepository.save(folder);
    }

    // -------- LIST CONTENTS --------
    public FolderContentsResponse getContents(java.util.UUID folderId, java.util.UUID userId) {

        Folder folder = null;

        if (folderId != null) {
            folder = folderRepository
                .findByIdAndOwnerId(folderId, userId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        }

        List<Folder> folders =
            (folder == null)
                ? folderRepository.findByOwnerIdAndParentIsNull(userId)
                : folderRepository.findByParentIdAndOwnerId(folder.getId(), userId);

        List<FileMetadata> files =
            fileRepository.findByFolderIdAndOwnerId(
                folder == null ? null : folder.getId(),
                userId
            );

        return new FolderContentsResponse(folders, files);
    }

    // -------- RENAME --------
    public void update(UUID folderId, UpdateFolderRequest request, UUID userId) {

        Folder folder = folderRepository
            .findByIdAndOwnerId(folderId, userId)
            .orElseThrow(() -> new RuntimeException("Folder not found"));

        folder.setName(request.getName());
        folderRepository.save(folder);
    }

    // -------- DELETE (RECURSIVE) --------
    public void delete(UUID folderId, UUID userId) {

        Folder folder = folderRepository
            .findByIdAndOwnerId(folderId, userId)
            .orElseThrow(() -> new RuntimeException("Folder not found"));

        deleteRecursive(folder, userId);
    }

    private void deleteRecursive(Folder folder, UUID userId) {

        // delete files
        List<FileMetadata> files =
            fileRepository.findByFolderIdAndOwnerId(folder.getId(), userId);

        for (FileMetadata file : files) {
            fileRepository.delete(file);
        }

        // delete subfolders
        List<Folder> subFolders =
            folderRepository.findByParentIdAndOwnerId(folder.getId(), userId);

        for (Folder sub : subFolders) {
            deleteRecursive(sub, userId);
        }

        folderRepository.delete(folder);
    }
}
