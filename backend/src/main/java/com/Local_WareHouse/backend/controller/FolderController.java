package com.Local_WareHouse.backend.controller;

import com.Local_WareHouse.backend.dto.CreateFolderRequest;
import com.Local_WareHouse.backend.dto.UpdateFolderRequest;
import com.Local_WareHouse.backend.service.FolderService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1//folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    // -------- CREATE --------
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CreateFolderRequest request,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(
            folderService.create(request, userId)
        );
    }

    // -------- LIST CONTENTS --------
    @GetMapping("/{id}")
    public ResponseEntity<?> contents(
            @PathVariable java.util.UUID id,
            Authentication auth) {

        java.util.UUID userId = (java.util.UUID) auth.getPrincipal();
        return ResponseEntity.ok(
            folderService.getContents(id, userId)
        );
    }

    // -------- UPDATE --------
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable java.util.UUID id,
            @RequestBody UpdateFolderRequest request,
            Authentication auth) {

        java.util.UUID userId = (java.util.UUID) auth.getPrincipal();
        folderService.update(id, request, userId);
        return ResponseEntity.ok().build();
    }

    // -------- DELETE --------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable java.util.UUID id,
            Authentication auth) {

        java.util.UUID userId = (java.util.UUID) auth.getPrincipal();
        folderService.delete(id, userId);
        return ResponseEntity.ok().build();
    }
}
