package com.Local_WareHouse.backend.dto;
import com.Local_WareHouse.backend.entity.Permission;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class UpdateShareRequest {

    private UUID resourceId;
    private UUID userId;
    private Permission permission;
}
