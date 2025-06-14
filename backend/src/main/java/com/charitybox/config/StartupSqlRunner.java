
package com.charitybox.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupSqlRunner {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StartupSqlRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void runSql() {
        jdbcTemplate.execute("ALTER TABLE collection_box_amounts ADD CONSTRAINT IF NOT EXISTS chk_amount_non_negative CHECK (amount >= 0)");
    }
}