package com.Local_WareHouse.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter @Setter
public class RevokeShareRequest {

    private UUID resourceId;
    private UUID userId;
}
