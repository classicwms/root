package com.tekclover.wms.api.transaction.repository;

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

    @Modifying
    @Query(value = " UPDATE PIBL SET PIBL.STATUS_ID = :statusId, \n" +
            "PIBL.STATUS_TEXT = :statusDescription, PIBL.UTD_BY = :updatedBy, PIBL.UTD_ON = :updatedOn FROM tblpreinboundline PIBL \n" +
            "INNER JOIN ( \n" +
            "SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_IB_NO, IB_LINE_NO, ITM_CODE, MFR_NAME \n" +
            "FROM tblinboundline \n" +
            "WHERE IS_DELETED = 0 \n" +
            "AND REF_FIELD_2 = 'TRUE' " +
            "AND STATUS_ID = :statusId AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId \n" +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo) X ON PIBL.C_ID = X.C_ID \n" +
            "AND PIBL.PLANT_ID = X.PLANT_ID \n" +
            "AND PIBL.LANG_ID = X.LANG_ID \n" +
            "AND PIBL.WH_ID = X.WH_ID \n" +
            "AND PIBL.REF_DOC_NO = X.REF_DOC_NO \n" +
            "AND PIBL.PRE_IB_NO = X.PRE_IB_NO \n" +
            "AND PIBL.ITM_CODE = X.ITM_CODE \n" +
            "AND PIBL.MFR_NAME = X.MFR_NAME \n" +
            "AND PIBL.IB_LINE_NO = X.IB_LINE_NO \n" +
            "AND PIBL.IS_DELETED = 0 ", nativeQuery = true)
    int updatePreInboundLine(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("preInboundNo") String preInboundNo,
                              @Param("statusId") Long statusId,
                              @Param("statusDescription") String statusDescription,
                              @Param("updatedBy") String updatedBy,
                              @Param("updatedOn") Date updatedOn);
}