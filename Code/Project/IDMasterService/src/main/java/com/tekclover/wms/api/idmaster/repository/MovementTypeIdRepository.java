package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface MovementTypeIdRepository extends JpaRepository<MovementTypeId,Long>, JpaSpecificationExecutor<MovementTypeId> {
	
	public List<MovementTypeId> findAll();
	public Optional<MovementTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String movementTypeId, String languageId, Long deletionIndicator);
}