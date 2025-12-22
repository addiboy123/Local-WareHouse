package com.Local_WareHouse.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import com.Local_WareHouse.backend.dto.*;
import com.Local_WareHouse.backend.entity.*;
import com.Local_WareHouse.backend.service.ShareService;
import java.util.UUID;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    // -------- SHARE FILE --------
    @PostMapping("/file")
    public ResponseEntity<?> shareFile(
            @RequestBody ShareRequest req,
            Authentication auth) {

        UUID ownerId = (UUID) auth.getPrincipal();
        shareService.shareFile(req, ownerId);
        return ResponseEntity.ok().build();
    }

    // -------- SHARE FOLDER --------
    @PostMapping("/folder")
    public ResponseEntity<?> shareFolder(
            @RequestBody ShareRequest req,
            Authentication auth) {

        UUID ownerId = (UUID) auth.getPrincipal();
        shareService.shareFolder(req, ownerId);
        return ResponseEntity.ok().build();
    }

    // -------- GET SHARED FILES --------
    @GetMapping("/files")
    public Page<FileMetadata> getSharedFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        return shareService.getSharedFiles(userId, page, size);
    }

    // -------- GET SHARED FOLDERS --------
    @GetMapping("/folders")
    public Page<Folder> getSharedFolders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        return shareService.getSharedFolders(userId, page, size);
    }

    // ---------- UPDATE FILE SHARE ----------
    @PutMapping("/file")
    public ResponseEntity<?> updateFileShare(
            @RequestBody UpdateShareRequest req,
            Authentication auth) {

        UUID ownerId = (UUID) auth.getPrincipal();
        shareService.updateFileShare(req, ownerId);
        return ResponseEntity.ok().build();
    }

    // ---------- REVOKE FILE SHARE ----------
    @DeleteMapping("/file")
    public ResponseEntity<?> revokeFileShare(
            @RequestBody RevokeShareRequest req,
            Authentication auth) {

        UUID ownerId = (UUID) auth.getPrincipal();
        shareService.revokeFileShare(req, ownerId);
        return ResponseEntity.ok().build();
    }

    // ---------- UPDATE FOLDER SHARE ----------
    @PutMapping("/folder")
    public ResponseEntity<?> updateFolderShare(
            @RequestBody UpdateShareRequest req,
            Authentication auth) {

        UUID ownerId = (UUID) auth.getPrincipal();
        shareService.updateFolderShare(req, ownerId);
        return ResponseEntity.ok().build();
    }

    // ---------- REVOKE FOLDER SHARE ----------
    @DeleteMapping("/folder")
    public ResponseEntity<?> revokeFolderShare(
            @RequestBody RevokeShareRequest req,
            Authentication auth) {

        UUID ownerId = (UUID) auth.getPrincipal();
        shareService.revokeFolderShare(req, ownerId);
        return ResponseEntity.ok().build();
    }
}
