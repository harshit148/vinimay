package org.xdev100.vinimay.repository;

import org.xdev100.vinimay.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Long> {
    Ticker findBySymbol(String symbol);
}
