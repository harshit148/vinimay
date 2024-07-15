/*
package org.xdev100.vinimay.db.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Service
@Slf4j
@DependsOn("SeedDb")
public class KlineViewRefresh {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate=10000)
    public void refreshView() {
        try {
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW klines_1m");
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW klines_1h");
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW klines_1w");
        log.info("Kline data refreshed");
        } catch(Exception e) {
            log.error("Error refreshing klines data");
            e.printStackTrace();
        }
    }


}
*/
