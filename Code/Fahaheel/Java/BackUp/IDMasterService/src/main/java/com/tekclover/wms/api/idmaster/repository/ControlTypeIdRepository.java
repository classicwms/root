package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.controltypeid.ControlTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface ControlTypeIdRepository extends JpaRepository<ControlTypeId,Long>, JpaSpecificationExecutor<ControlTypeId> {
	
	public List<ControlTypeId> findAll();
	public Optional<ControlTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndControlTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String controlTypeId, String languageId, Long deletionIndicator);
}