package com.Local_WareHouse.backend.dto;

import com.Local_WareHouse.backend.entity.FileMetadata;
import com.Local_WareHouse.backend.entity.Folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FolderContentsResponse {

    private List<Folder> folders;
    private List<FileMetadata> files;
}
