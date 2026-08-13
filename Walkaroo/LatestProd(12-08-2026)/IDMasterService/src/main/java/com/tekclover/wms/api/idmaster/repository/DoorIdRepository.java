package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.doorid.DoorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface DoorIdRepository extends JpaRepository<DoorId,Long>, JpaSpecificationExecutor<DoorId> {
	
	public List<DoorId> findAll();
	public Optional<DoorId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDoorIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String doorId, String languageId, Long deletionIndicator);
}