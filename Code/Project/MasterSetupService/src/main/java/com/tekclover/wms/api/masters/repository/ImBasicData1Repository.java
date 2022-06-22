package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;

@Repository
@Transactional
public interface ImBasicData1Repository extends PagingAndSortingRepository<ImBasicData1,Long>, 
JpaSpecificationExecutor<ImBasicData1> {

	public Optional<ImBasicData1> findByItemCodeAndWarehouseIdAndDeletionIndicator(String itemCode, String warehouseId, Long deletionIndicator);
}