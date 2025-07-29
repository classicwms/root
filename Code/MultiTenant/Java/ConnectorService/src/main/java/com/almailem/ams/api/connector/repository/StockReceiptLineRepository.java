package com.almailem.ams.api.connector.repository;

import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptHeader;
import com.almailem.ams.api.connector.model.stockreceipt.StockReceiptLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface StockReceiptLineRepository extends JpaRepository<StockReceiptLine, String>,
        JpaSpecificationExecutor<StockReceiptLine> {

}
