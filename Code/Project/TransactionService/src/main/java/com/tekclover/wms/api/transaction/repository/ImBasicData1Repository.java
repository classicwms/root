package com.tekclover.wms.api.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;

@Repository
@Transactional
public interface ImBasicData1Repository extends JpaRepository<ImBasicData1,Long>, 
 JpaSpecificationExecutor<ImBasicData1> {

	public ImBasicData1 findByItemCodeAndWarehouseIdAndDeletionIndicator(String itemCode, 
			String warehouseId, Long deletionIndicator);
}