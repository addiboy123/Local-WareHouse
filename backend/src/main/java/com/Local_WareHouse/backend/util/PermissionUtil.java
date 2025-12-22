package com.Local_WareHouse.backend.util;
import org.springframework.stereotype.Component;

import com.Local_WareHouse.backend.entity.Permission;

@Component
public class PermissionUtil {

    public boolean canRead(Permission p) {
        return p == Permission.READ || p == Permission.WRITE;
    }

    public boolean canWrite(Permission p) {
        return p == Permission.WRITE;
    }
}
