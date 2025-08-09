package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.threepl.pricelistassignment.PriceListAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface PriceListAssignmentRepository extends JpaRepository<PriceListAssignment,String>, JpaSpecificationExecutor<PriceListAssignment> {
    Optional<PriceListAssignment> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndPriceListIdAndLanguageIdAndDeletionIndicator(String companyCodeId, String plantId, String warehouseId, String partnerCode, Long priceListId, String languageId, Long deletionIndicator);
}
