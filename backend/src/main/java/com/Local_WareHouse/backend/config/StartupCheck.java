package com.Local_WareHouse.backend.config;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;

@Component
public class StartupCheck {
    private final DataSource dataSource;

    public StartupCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void checkDbConnection() throws Exception {
        System.out.println(
            "DB Connected: " +
            dataSource.getConnection().getMetaData().getURL()
        );
    }
}
