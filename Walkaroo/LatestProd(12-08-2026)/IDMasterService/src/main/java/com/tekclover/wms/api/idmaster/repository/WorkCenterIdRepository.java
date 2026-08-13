package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.workcenterid.WorkCenterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface WorkCenterIdRepository extends JpaRepository<WorkCenterId,Long>, JpaSpecificationExecutor<WorkCenterId> {
	
	public List<WorkCenterId> findAll();
	public Optional<WorkCenterId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndWorkCenterIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String workCenterId, String languageId, Long deletionIndicator);
}