package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.dto.PutAwayStrategyDetails;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
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
public interface PreInboundLineV2Repository extends JpaRepository<PreInboundLineEntityV2, Long>,
        JpaSpecificationExecutor<PreInboundLineEntityV2>, StreamableJpaSpecificationRepository<PreInboundLineEntityV2> {

    public List<PreInboundLineEntityV2> findByPreInboundNoAndDeletionIndicator(
            String preInboundNo, Long deletionIndicator);

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreInboundLineEntityV2 ib SET ib.statusId = :statusId WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
    void updatePreInboundLineStatus(@Param("warehouseId") String warehouseId,
                                    @Param("refDocNumber") String refDocNumber, @Param("statusId") Long statusId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreInboundLineEntityV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updatePreInboundLineStatus(@Param("warehouseId") String warehouseId,
                                    @Param("companyCode") String companyCode,
                                    @Param("plantId") String plantId,
                                    @Param("languageId") String languageId,
                                    @Param("refDocNumber") String refDocNumber,
                                    @Param("statusId") Long statusId,
                                    @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreInboundLineEntityV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and \n" +
            "ib.plantId = :plantId and ib.languageId = :languageId and ib.itemCode = :itemCode and ib.manufacturerName = :manufacturerName and ib.lineNo = :lineNo")
    void updatePreInboundLineStatus(@Param("warehouseId") String warehouseId,
                                    @Param("companyCode") String companyCode,
                                    @Param("plantId") String plantId,
                                    @Param("languageId") String languageId,
                                    @Param("refDocNumber") String refDocNumber,
                                    @Param("statusId") Long statusId,
                                    @Param("statusDescription") String statusDescription,
                                    @Param("itemCode") String itemCode,
                                    @Param("manufacturerName") String manufacturerName,
                                    @Param("lineNo") Long lineNo);
    public List<PreInboundLineEntityV2> findByWarehouseIdAndPreInboundNoAndDeletionIndicator(String warehouseId,
                                                                                             String preInboundNo, Long deletionIndicator);

    Optional<PreInboundLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, Long lineNo, String itemCode, Long deletionIndicator);

    List<PreInboundLineEntityV2> findByWarehouseIdAndCompanyCodeAndPlantIdAndLanguageIdAndPreInboundNoAndDeletionIndicator(
            String warehouseId, String companyCode, String plantId, String languageId,
            String preInboundNo, Long deletionIndicator);

    List<PreInboundLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<PreInboundLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Transactional
    @Procedure(procedureName = "preinboundline_status_update_ib_cnf_proc")
    public void updatePreInboundLineStatusUpdateInboundConfirmProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

    @Transactional
    @Procedure(procedureName = "pre_ib_line_status_update_proc")
    public void updatePreInboundLineStatusUpdateProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription);

    // PGI Reversal process
    PreInboundLineEntityV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndLineNoAndDeletionIndicator(
      String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preInboundNo, String itemCode, Long lineNumber, Long deletionIndicator
    );

    @Modifying
    @Query(value = "UPDATE tblpreinboundline set status_id = :statusId, status_text = :statusText where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber AND pre_ib_no = :preInboundNo " +
            "AND itm_code = :itemCode AND ib_line_no = :lineNo and is_deleted = 0", nativeQuery = true)
    void updatePreInboundLinePGI(@Param("companyCodeId") String companyCodeId,
                                 @Param("plantId") String plantId,
                                 @Param("warehouseId") String warehouseId,
                                 @Param("refDocNumber") String refDocNumber,
                                 @Param("preInboundNo") String preInbound,
                                 @Param("itemCode") String itemCode,
                                 @Param("lineNo") Long lineNo,
                                 @Param("statusId") Long statusId,
                                 @Param("statusText") String statusText);

    @Modifying
    @Query(value = "UPDATE tblpreinboundline set status_id = :statusId, status_text = :statusText where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber " +
            "AND itm_code = :itemCode AND ib_line_no = :lineNo and is_deleted = 0", nativeQuery = true)
    void updatePreInboundLine(@Param("companyCodeId") String companyCodeId,
                                 @Param("plantId") String plantId,
                                 @Param("warehouseId") String warehouseId,
                                 @Param("refDocNumber") String refDocNumber,
                                 @Param("itemCode") String itemCode,
                                 @Param("lineNo") Long lineNo,
                                 @Param("statusId") Long statusId,
                                 @Param("statusText") String statusText);

    boolean existsByCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator
    );

//    SELECT p.maxCapacity AS maxCapacity, p.superMaxCapacity AS superMaxCapacity " +
//            "FROM PutawayStrategy p " +
//            "WHERE p.articleNo = :articleNo AND p.gender = :gender

    @Query(value = "SELECT p.max_capacity AS maxCapacity, p.super_max_capacity AS superMaxCapacity, p.proposed_bin AS storageBin " +
            "FROM tblputawaystrategy p WHERE p.article = :article and p.gender = :gender and is_deleted = 0", nativeQuery = true)
    PutAwayStrategyDetails getPutAwayStrategyDetails(@Param("article") String articleNo,
                                                     @Param("gender") String gender);
}