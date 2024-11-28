package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.barcode.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface BarcodeRepository extends JpaRepository<Barcode, Long>, JpaSpecificationExecutor<Barcode> {

    public List<Barcode> findAll();

    public Optional<Barcode> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndMethodAndBarcodeTypeIdAndBarcodeSubTypeIdAndLevelIdAndLevelReferenceAndProcessIdAndDeletionIndicator(
            String languageId, String companyId, String plantId, String warehouseId, String method,
            Long barcodeTypeId, Long barcodeSubTypeId, Long levelId, String levelReference, Long processId,
            Long deletionIndicator);
}