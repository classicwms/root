package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.trans.InventoryTrans;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface InventoryTransRepository extends PagingAndSortingRepository<InventoryTrans,Long>, JpaSpecificationExecutor<InventoryTrans> {
	
	String UPGRADE_SKIPLOCKED = "-2";
	
	public List<InventoryTrans> findAll();
	
	public List<InventoryTrans> findByReRun(Long l);
}