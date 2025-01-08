package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.handlingequipment.HandlingEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface HandlingEquipmentRepository extends JpaRepository<HandlingEquipment, Long>, JpaSpecificationExecutor<HandlingEquipment> {

    public Optional<HandlingEquipment> findByHandlingEquipmentId(String handlingEquipmentId);

    public Optional<HandlingEquipment> findByHeBarcodeAndWarehouseIdAndDeletionIndicator(String heBarcode, String warehouseId, Long deletionIndicator);

    public Optional<HandlingEquipment> findByHandlingEquipmentIdAndWarehouseIdAndDeletionIndicator(String handlingEquipmentId,
                                                                                                   String warehouseId, long l);

    Optional<HandlingEquipment> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingEquipmentIdAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String handlingEquipmentId, String languageId, Long deletionIndicator);

    Optional<HandlingEquipment> findByHeBarcodeAndCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
            String heBarcode, String companyCodeId, String plantId, String languageId, String warehouseId, Long deletionIndicator);
}