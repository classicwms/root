package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.impacking.ImPacking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ImPackingRepository extends JpaRepository<ImPacking, Long>, JpaSpecificationExecutor<ImPacking> {

    Optional<ImPacking> findByPackingMaterialNo(String packingMaterialNo);

    Optional<ImPacking> findByPackingMaterialNoAndCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(String packingMaterialNo, String companyCodeId, String languageId, String plantId, String warehouseId, String itemCode, Long deletionIndicator);
}