package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.List;

import com.tekclover.wms.api.inbound.transaction.model.inbound.stock.v2.InventoryStockV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface InventoryStockRepository extends PagingAndSortingRepository<InventoryStockV2,Long>,
        JpaSpecificationExecutor<InventoryStockV2>, StreamableJpaSpecificationRepository<InventoryStockV2> {

    public List<InventoryStockV2> findAll();

    @Query (value = "SELECT (SUM(INV_QTY) + SUM(ALLOC_QTY)) AS SUM_VALUE FROM tblinventorystock \r\n"
            + " WHERE ITM_CODE IN :itemCode AND BIN_CL_ID = 1 \r\n"
            + " GROUP BY ITM_CODE", nativeQuery = true)
    public Double findSumOfInventoryQtyAndAllocQty (@Param(value = "itemCode") List<String> itemCode);
}