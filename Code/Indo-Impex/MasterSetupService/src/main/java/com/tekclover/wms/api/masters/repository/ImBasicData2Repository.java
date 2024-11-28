package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.imbasicdata2.ImBasicData2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ImBasicData2Repository extends JpaRepository<ImBasicData2, Long>, JpaSpecificationExecutor<ImBasicData2> {

    Optional<ImBasicData2> findByItemCode(String itemCode);

    Optional<ImBasicData2> findByItemCodeAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(String itemCode, String companyCodeId, String plantId, String warehouseId, String languageId, Long deletionIndicator);
}