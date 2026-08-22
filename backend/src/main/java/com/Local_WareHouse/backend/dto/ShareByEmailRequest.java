package com.Local_WareHouse.backend.dto;

import com.Local_WareHouse.backend.entity.Permission;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ShareByEmailRequest {
    private UUID resourceId;
    private String email;
    private Permission permission = Permission.READ;
}