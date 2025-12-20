package com.Local_WareHouse.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
import java.util.Map;
import com.Local_WareHouse.backend.entity.Permission;

@Getter @Setter
public class ShareRequest {
    private UUID resourceId;
    private Map<java.util.UUID, Permission> users;
}

