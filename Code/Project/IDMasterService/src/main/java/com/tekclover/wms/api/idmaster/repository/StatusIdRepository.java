package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.statusid.StatusId;

@Repository
@Transactional
public interface StatusIdRepository extends JpaRepository<StatusId,Long>, JpaSpecificationExecutor<StatusId> {
	
	public List<StatusId> findAll();
	public Optional<StatusId> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, Long statusId, Long deletionIndicator);
}