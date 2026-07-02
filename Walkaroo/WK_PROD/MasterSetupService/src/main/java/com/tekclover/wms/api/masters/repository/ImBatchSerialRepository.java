package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.imbatchserial.ImBatchSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ImBatchSerialRepository extends JpaRepository<ImBatchSerial,String>, JpaSpecificationExecutor<ImBatchSerial> {
    Optional<ImBatchSerial> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndStorageMethodAndDeletionIndicator(
            String languageId,String companyCodeId,String plantId,String warehouseId,String itemCode,String storageMethodId,Long deletionIndicator);

    Optional<ImBatchSerial> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, Long deletionIndicator);


}
