package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.businesspartner.v2.BusinessPartnerV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
@Transactional
public interface BusinessPartnerV2Repository extends JpaRepository<BusinessPartnerV2, Long>, JpaSpecificationExecutor<BusinessPartnerV2> {

    Optional<BusinessPartnerV2> findByPartnerCode(String partnerCode);

    Optional<BusinessPartnerV2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndBusinessPartnerTypeAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String partnerCode, Long businessPartnerType, String languageId, Long deletionIndicator);

    BusinessPartnerV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String partnerCode, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE TBLBUSINESSPARTNER set \n" +
            "processedStatusId = 10, orderProcessedOn = :date  \r\n"
            + " WHERE PickListNo = :pickListNo ", nativeQuery = true)
    public void updateBusinessPartner(
            @Param(value = "pickListNo") String pickListNo,
            @Param(value = "date") Date date);
}