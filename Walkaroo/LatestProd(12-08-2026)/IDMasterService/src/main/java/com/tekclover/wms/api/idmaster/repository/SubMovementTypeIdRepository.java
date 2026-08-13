package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.submovementtypeid.SubMovementTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface SubMovementTypeIdRepository extends JpaRepository<SubMovementTypeId,Long>, JpaSpecificationExecutor<SubMovementTypeId> {
	
	public List<SubMovementTypeId> findAll();
	public Optional<SubMovementTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndSubMovementTypeIdAndMovementTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String subMovementTypeId, String movementTypeId, String languageId, Long deletionIndicator);
}