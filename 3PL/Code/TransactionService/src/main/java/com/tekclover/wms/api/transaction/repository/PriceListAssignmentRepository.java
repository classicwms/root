package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.model.threepl.pricelist.PriceListAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface PriceListAssignmentRepository extends JpaRepository<PriceListAssignment,String>, JpaSpecificationExecutor<PriceListAssignment> {
    Optional<PriceListAssignment> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndPriceListIdAndLanguageIdAndDeletionIndicator(String companyCodeId, String plantId, String warehouseId, String partnerCode, Long priceListId, String languageId, Long deletionIndicator);

    PriceListAssignment findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String partnerCode, Long deletionIndicator);

    @Query(value = "select * from tblpricelistassignment \n" +
            "where (COALESCE(:businessPartnerCode, null) IS NULL OR (partner_code IN (:businessPartnerCode))) and \n" +
            "(COALESCE(:companyCode, null) IS NULL OR (c_id IN (:companyCode))) and (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) " +
            "and (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) ", nativeQuery = true)
    PriceListAssignment getPartnerCode(@Param("companyCode") Long companyCode,
                                       @Param("plantId") String plantId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("languageId") String languageId,
                                       @Param("businessPartnerCode") String businessPartnerCode);

    @Query(value = "select * from tblpricelistassignment \n" +
            "where (COALESCE(:businessPartnerCode, null) IS NULL OR (partner_code IN (:businessPartnerCode))) and \n" +
            "(COALESCE(:companyCode, null) IS NULL OR (c_id IN (:companyCode))) and (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) " +
            "and (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) ", nativeQuery = true)
    PriceListAssignment getPartnerCodeInv(@Param("companyCode") String companyCode,
                                       @Param("plantId") String plantId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("languageId") String languageId,
                                       @Param("businessPartnerCode") String businessPartnerCode);
}
