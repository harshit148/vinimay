package org.xdev100.vinimay.db.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SeedDb {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeDatabase() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS share_price (
                id SERIAL PRIMARY KEY,
                time TIMESTAMP WITH TIME ZONE NOT NULL,
                price DOUBLE PRECISION,
                volume DOUBLE PRECISION,
                currency_code VARCHAR(10),
                share_symbol VARCHAR(10)
            );
        """;

        String createView1mSql = """
            CREATE MATERIALIZED VIEW IF NOT EXISTS klines_1m AS
            SELECT
                time_bucket('1 minute', time) AS bucket,
                first(price, time) AS open,
                max(price) AS high,
                min(price) AS low,
                last(price, time) AS close,
                sum(volume) AS volume,
                currency_code,
                share_symbol
            FROM share_price
            GROUP BY bucket, currency_code, share_symbol;
        """;

        String createView1hSql = """
            CREATE MATERIALIZED VIEW IF NOT EXISTS klines_1h AS
            SELECT
                time_bucket('1 hour', time) AS bucket,
                first(price, time) AS open,
                max(price) AS high,
                min(price) AS low,
                last(price, time) AS close,
                sum(volume) AS volume,
                currency_code,
                share_symbol
            FROM share_price
            GROUP BY bucket, currency_code, share_symbol;
        """;

        String createView1wSql = """
            CREATE MATERIALIZED VIEW IF NOT EXISTS klines_1w AS
            SELECT
                time_bucket('1 week', time) AS bucket,
                first(price, time) AS open,
                max(price) AS high,
                min(price) AS low,
                last(price, time) AS close,
                sum(volume) AS volume,
                currency_code,
                share_symbol
            FROM share_price
            GROUP BY bucket, currency_code, share_symbol;
        """;

        jdbcTemplate.execute(createTableSql);
        jdbcTemplate.execute(createView1mSql);
        jdbcTemplate.execute(createView1hSql);
        jdbcTemplate.execute(createView1wSql);
        log.info("Database initialized successfully");
        }
}

