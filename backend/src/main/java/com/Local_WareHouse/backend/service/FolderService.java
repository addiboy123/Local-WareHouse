package com.Local_WareHouse.backend.service;

import com.Local_WareHouse.backend.dto.*;
import com.Local_WareHouse.backend.entity.Folder;
import com.Local_WareHouse.backend.entity.FileMetadata;
import com.Local_WareHouse.backend.entity.User;
import com.Local_WareHouse.backend.repository.FolderRepository;
import com.Local_WareHouse.backend.repository.FileRepository;
import com.Local_WareHouse.backend.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public FolderContentsResponse getContents(java.util.UUID folderId, java.util.UUID userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Folder folder = null;

        if (folderId != null) {
            folder = folderRepository
                .findByIdAndOwnerId(folderId, userId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        }

        List<Folder> folders =
            (folder == null)
                ? folderRepository.findByOwnerIdAndParentIsNull(userId, pageable).getContent()
                : folderRepository.findByParentIdAndOwnerId(folder.getId(), userId, pageable).getContent();

        List<FileMetadata> files =
            (folder == null)
                ? fileRepository.findByFolderIsNullAndOwnerId(userId, pageable).getContent()
                : fileRepository.findByFolderIdAndOwnerId(folder.getId(), userId, pageable).getContent();

        return new FolderContentsResponse(
            folder != null ? folder.getId() : null,
            folder != null ? folder.getName() : "Root",
            folders,
            files
        );
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

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

        // delete files
        List<FileMetadata> files =
            fileRepository.findByFolderIdAndOwnerId(folder.getId(), userId, pageable).getContent();

        for (FileMetadata file : files) {
            fileRepository.delete(file);
        }

        // delete subfolders
        List<Folder> subFolders =
            folderRepository.findByParentIdAndOwnerId(folder.getId(), userId, pageable).getContent();

        for (Folder sub : subFolders) {
            deleteRecursive(sub, userId);
        }

        folderRepository.delete(folder);
    }

    public FolderContentsResponse getRootContents(
        UUID userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        List<Folder> folders =
            folderRepository.findByParentIsNullAndOwnerId(userId, pageable)
                .getContent();

        List<FileMetadata> files =
            fileRepository.findByFolderIsNullAndOwnerId(userId, pageable)
                .getContent();

        return new FolderContentsResponse(
            null,
            "Root",
            folders,
            files
        );
    }

    public Page<Folder> searchFolders(UUID userId, String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return folderRepository.findByNameContainingIgnoreCaseAndOwnerId(query, userId, pageable);
    }

}
