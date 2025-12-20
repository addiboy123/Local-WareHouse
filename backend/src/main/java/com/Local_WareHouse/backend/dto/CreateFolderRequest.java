package com.Local_WareHouse.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class CreateFolderRequest {
    private String name;
    private UUID parentId; // nullable
}