package com.tekclover.wms.api.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicHeader;

@Repository
@Transactional
public interface PeriodicHeaderRepository extends JpaRepository<PeriodicHeader,Long>, JpaSpecificationExecutor<PeriodicHeader> {
	
	public List<PeriodicHeader> findAll();
	
	public PeriodicHeader findByCompanyCodeAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNo(
			String companyCode, String plantId, String warehouseId, Long cycleCountTypeId, String cycleCountNo);

	public PeriodicHeader findByCycleCountNo(String cycleCountNo);
}