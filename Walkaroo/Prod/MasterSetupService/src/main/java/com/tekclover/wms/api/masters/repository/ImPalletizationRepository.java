package com.tekclover.wms.api.masters.repository;


import com.tekclover.wms.api.masters.model.impalletization.ImPalletization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ImPalletizationRepository extends JpaRepository<ImPalletization,String>, JpaSpecificationExecutor<ImPalletization> {

    List<ImPalletization> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndPalletizationLevelAndDeletionIndicator(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String palletizationLevel, Long deletionIndicator);

    List<ImPalletization> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndDeletionIndicator(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, Long deletionIndicator);

}
