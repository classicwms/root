package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StagingHeaderV2Repository extends JpaRepository<StagingHeaderV2, Long>,
        JpaSpecificationExecutor<StagingHeaderV2>, StreamableJpaSpecificationRepository<StagingHeaderV2> {

    Optional<StagingHeaderV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, String stagingNo, Long deletionIndicator);

    List<StagingHeaderV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, Long deletionIndicator);

    List<StagingHeaderV2> findByWarehouseIdAndStatusIdAndDeletionIndicator(
            String warehouseId, Long statusId, Long deletionIndicator);

    Optional<StagingHeaderV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumber, Long deletionIndicator);

    StagingHeaderV2 findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber, Long deletionIndicator);

    StagingHeaderV2 findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE StagingHeaderV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updateStagingHeaderStatus(@Param("warehouseId") String warehouseId,
                                   @Param("companyCode") String companyCode,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("statusId") Long statusId,
                                   @Param("statusDescription") String statusDescription);


    @Modifying
    @Query(value = "UPDATE tblstagingheader SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, " +
            "ST_CNF_BY = :updatedBy, ST_CNF_ON = :updatedOn " +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId " +
            "AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
            "AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo", nativeQuery = true)
    void updateStagingHeader(@Param("statusId") Long statusId, @Param("statusDescription") String statusDescription,
                             @Param("updatedBy") String updatedBy, @Param("updatedOn") Date updatedOn,
                             @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId,
                             @Param("languageId") String languageId, @Param("warehouseId") String warehouseId,
                             @Param("refDocNumber") String refDocNumber, @Param("preInboundNo") String preInboundNo);

}