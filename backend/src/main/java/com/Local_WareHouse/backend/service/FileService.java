package com.Local_WareHouse.backend.service;

import com.Local_WareHouse.backend.entity.FileMetadata;
import com.Local_WareHouse.backend.entity.FileShare;
import com.Local_WareHouse.backend.entity.User;
import com.Local_WareHouse.backend.exception.ForbiddenException;
import com.Local_WareHouse.backend.exception.ResourceNotFoundException;
import com.Local_WareHouse.backend.repository.FileRepository;
import com.Local_WareHouse.backend.repository.UserRepository;
import com.Local_WareHouse.backend.repository.FileShareRepository;
import com.Local_WareHouse.backend.entity.Permission;
import com.Local_WareHouse.backend.dto.UpdateFileRequest;
import com.Local_WareHouse.backend.util.PermissionUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileShareRepository fileShareRepo;
    private final PermissionUtil permissionUtil;

    private static  String STORAGE_ROOT;

    public FileService(FileRepository fileRepository, UserRepository userRepository,
                       FileShareRepository fileShareRepo,
                        PermissionUtil permissionUtil,
                       @Value("${file.storage.root}") String storageRoot) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.fileShareRepo = fileShareRepo;
        this.permissionUtil = permissionUtil;
        STORAGE_ROOT = storageRoot;
    }

    public FileMetadata upload(
            MultipartFile file,
            UUID folderId,
            UUID userId) {

        try {
            String storageName = UUID.randomUUID().toString();
            Path userDir = Paths.get(STORAGE_ROOT, userId.toString());
            Files.createDirectories(userDir);

            Path target = userDir.resolve(storageName);
            Files.copy(file.getInputStream(), target);

            FileMetadata meta = new FileMetadata();
            meta.setOriginalName(file.getOriginalFilename());
            meta.setStoragePath(target.toString());
            meta.setSize(file.getSize());

            User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            meta.setOwner(owner);
            meta.setMimeType(file.getContentType());

            return fileRepository.save(meta);

        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }
    }

    public ResponseEntity<Resource> download(UUID fileId, UUID userId) {

    FileMetadata file = fileRepository
        .findById(fileId)
        .orElseThrow(() -> new ResourceNotFoundException("File not found"));

    if (!file.getOwner().getId().equals(userId)) {

        FileShare share = fileShareRepo
            .findByFileIdAndSharedWithId(fileId, userId)
            .orElseThrow(() -> new ForbiddenException("No access"));

        if (!permissionUtil.canRead(share.getPermission())) {
            throw new ForbiddenException("Read permission required");
        }
    }

    Resource resource = new FileSystemResource(file.getStoragePath());

    return ResponseEntity.ok()
        .header("Content-Disposition",
            "attachment; filename=\"" + file.getOriginalName() + "\"")
        .body(resource);
}


    public void delete(UUID fileId, UUID userId) {

        FileMetadata file = fileRepository
            .findByIdAndOwnerId(fileId, userId)
            .orElseThrow(() -> new RuntimeException("File not found"));

        try {
            Files.deleteIfExists(Paths.get(file.getStoragePath()));
            fileRepository.delete(file);
        } catch (Exception e) {
            throw new RuntimeException("Delete failed");
        }
    }

    public void update(UUID fileId, UpdateFileRequest req, UUID userId) {

    FileMetadata file = fileRepository
        .findById(fileId)
        .orElseThrow(() -> new ResourceNotFoundException("File not found"));

    if (!file.getOwner().getId().equals(userId)) {

        FileShare share = fileShareRepo
            .findByFileIdAndSharedWithId(fileId, userId)
            .orElseThrow(() -> new ForbiddenException("No access"));

        if (share.getPermission() != Permission.WRITE) {
            throw new ForbiddenException("Write permission required");
        }
    }

    file.setOriginalName(req.getName());
    fileRepository.save(file);
}

    public Page<FileMetadata> searchFiles(UUID userId, String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fileRepository.findByOriginalNameContainingIgnoreCaseAndOwnerId(query, userId, pageable);
    }

}