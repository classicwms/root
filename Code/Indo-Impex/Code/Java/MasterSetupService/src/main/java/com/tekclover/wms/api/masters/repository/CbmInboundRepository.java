package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.threepl.cbminbound.CbmInbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface CbmInboundRepository extends JpaRepository<CbmInbound, String>, JpaSpecificationExecutor<CbmInbound> {

    Optional<CbmInbound> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndLanguageIdAndDeletionIndicator(String companyCodeId, String plantId, String warehouseId, String itemCode, String languageId, Long deletionIndicator);
}