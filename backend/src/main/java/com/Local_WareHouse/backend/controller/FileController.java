package com.Local_WareHouse.backend.controller;

import com.Local_WareHouse.backend.service.FileService;
import com.Local_WareHouse.backend.dto.UpdateFileRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // -------- UPLOAD --------
    @PostMapping
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) UUID folderId,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        fileService.upload(file, folderId, userId);
        return ResponseEntity.ok().build();
    }

    // -------- DOWNLOAD --------
    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable UUID id,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        return fileService.download(id, userId);
    }

    // -------- DELETE --------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable UUID id,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        fileService.delete(id, userId);
        return ResponseEntity.ok().build();
    }

    // -------- UPDATE --------

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestBody UpdateFileRequest request,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        fileService.update(id, request, userId);
        return ResponseEntity.ok().build();
    }

    // -------- SEARCH FILES --------
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(
            fileService.searchFiles(userId, query, page, size)
        );
    }
}
