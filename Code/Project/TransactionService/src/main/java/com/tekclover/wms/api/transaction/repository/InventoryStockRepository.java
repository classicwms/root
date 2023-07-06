package com.tekclover.wms.api.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.stock.InventoryStock;

@Repository
@Transactional
public interface InventoryStockRepository extends PagingAndSortingRepository<InventoryStock,Long>, JpaSpecificationExecutor<InventoryStock> {
	
	public List<InventoryStock> findAll();
}