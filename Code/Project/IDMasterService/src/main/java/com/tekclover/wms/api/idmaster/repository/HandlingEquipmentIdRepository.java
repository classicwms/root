package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.handlingequipmentid.HandlingEquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface HandlingEquipmentIdRepository extends JpaRepository<HandlingEquipmentId,Long>, JpaSpecificationExecutor<HandlingEquipmentId> {
	
	public List<HandlingEquipmentId> findAll();
	public Optional<HandlingEquipmentId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingEquipmentIdAndHandlingEquipmentNumberAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String handlingEquipmentId,String handlingEquipmentNumber, String languageId, Long deletionIndicator);
}