package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.handlingequipment.HandlingEquipment;

@Repository
@Transactional
public interface HandlingEquipmentRepository extends JpaRepository<HandlingEquipment,Long>, JpaSpecificationExecutor<HandlingEquipment> {

	Optional<HandlingEquipment> findByHandlingEquipmentId(String handlingEquipmentId);
	
	public Optional<HandlingEquipment> findByHeBarcodeAndWarehouseIdAndDeletionIndicator(String heBarcode, String warehouseId, Long deletionIndicator);
}