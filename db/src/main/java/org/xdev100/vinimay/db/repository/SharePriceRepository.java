package org.xdev100.vinimay.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xdev100.vinimay.db.model.SharePrice;

public interface SharePriceRepository extends JpaRepository<SharePrice, Long> {
}
