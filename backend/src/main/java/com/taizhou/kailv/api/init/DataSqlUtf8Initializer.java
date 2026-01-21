package com.taizhou.kailv.api.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

// Disabled: no longer auto-loading schema/data on startup
//@Component
@Order(1)
public class DataSqlUtf8Initializer implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(DataSqlUtf8Initializer.class);
    private final JdbcTemplate jdbcTemplate;

    public DataSqlUtf8Initializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            // First run schema.sql then data.sql to ensure tables exist
            String[] resources = new String[]{"schema.sql", "data.sql"};
            for (String rname : resources) {
                ClassPathResource res = new ClassPathResource(rname);
                if (!res.exists()) {
                    log.debug("{} not found on classpath, skipping", rname);
                    continue;
                }

                StringBuilder sb = new StringBuilder();
                try (InputStream is = res.getInputStream();
                     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                     BufferedReader br = new BufferedReader(isr)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                }

                String sql = sb.toString();
                if (sql.startsWith("\uFEFF")) {
                    sql = sql.substring(1);
                }

                String[] statements = sql.split(";");
                for (String st : statements) {
                    String s = st.trim();
                    if (s.isEmpty()) continue;
                    try {
                        jdbcTemplate.execute(s);
                    } catch (Exception e) {
                        log.warn("Failed to execute SQL statement from {} ; continuing. stmt='{}'", rname, s);
                        log.debug("Cause:", e);
                    }
                }
                log.info("Programmatic {} initialization completed.", rname);
            }
        } catch (Exception e) {
            log.error("Error during programmatic data.sql initialization", e);
        }
    }
}
