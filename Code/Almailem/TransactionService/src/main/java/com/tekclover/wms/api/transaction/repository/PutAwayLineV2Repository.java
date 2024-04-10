package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;


@Repository
@Transactional
public interface PutAwayLineV2Repository extends JpaRepository<PutAwayLineV2, Long>,
        JpaSpecificationExecutor<PutAwayLineV2>, StreamableJpaSpecificationRepository<PutAwayLineV2> {


    Optional<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String goodsReceiptNo, String preInboundNo, String refDocNumber,
            String putAwayNumber, Long lineNo, String itemCode, String proposedStorageBin,
            List<String> confirmedStorageBin, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, Long lineNo, String itemCode, Long deletionIndicator);

    /**
     * @param companyId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    @Query(value = "SELECT COUNT(*) FROM tblputawayline WHERE LANG_ID = :languageId AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (20, 22) AND IS_DELETED = 0", nativeQuery = true)
    public long getPutawayLineCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("languageId") String languageId,
            @Param("preInboundNo") String preInboundNo,
            @Param("refDocNumber") String refDocNumber);
    
    
    @Query(value = "SELECT SUM(PA_CNF_QTY) FROM tblputawayline WHERE LANG_ID = :languageId AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (20) AND IS_DELETED = 0", nativeQuery = true)
    public double getSumOfPutawayLineQtyByStatusId20(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("languageId") String languageId,
            @Param("preInboundNo") String preInboundNo,
            @Param("refDocNumber") String refDocNumber);

    /**
     * @param companyId
     * @param plantId
     * @param warehouseId
     * @param putAwayNumber
     * @param refDocNumber
     * @param statusId
     * @return
     */
    @Query(value = "SELECT COUNT(*) FROM tblputawayline WHERE LANG_ID = :languageId AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PA_NO = :putAwayNumber AND REF_DOC_NO = :refDocNumber AND STATUS_ID = :statusId AND IS_DELETED = 0", nativeQuery = true)
    public long getPutawayLineCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("languageId") String languageId,
            @Param("putAwayNumber") String putAwayNumber,
            @Param("refDocNumber") String refDocNumber,
            @Param("statusId") Long statusId);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String refDocNumber, String putAwayNumber, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String refDocNumber, Long deletionIndicator);

    @Query(value = "SELECT * from tblputawayheader \n"
            + "where pa_no = :putAwayNumber and is_deleted = 0", nativeQuery = true)
    public PutAwayHeader getPutAwayHeader(@Param(value = "putAwayNumber") String putAwayNumber);

//    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndProposedStorageBinAndStatusIdAndCreatedOnBetweenAndDeletionIndicator(
//            String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
//            String manufacturerName, String storageBin, Long statusId, Date stockCountDate, Date date, Long deletionIndicator);
    
    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndProposedStorageBinAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
            String manufacturerName, String storageBin, Long statusId, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, Long statusId, Long deletionIndicator);

    PutAwayLineV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, String itemCode, String manufacturerName, Long lineNumber, Long statusId, Long deletionIndicator);

    List<PutAwayLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<PutAwayLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    List<PutAwayLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, String itemCode, String manufacturerName, Long lineNumber, Long statusId, String packBarcodes, Long deletionIndicator);
    List<PutAwayLineV2> findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber, String preInboundNo,
            String itemCode, String manufacturerName, Long lineNumber, Long statusId, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PutAwayLineV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId and ib.statusId = 20")
    void updatePutawayLineStatus(@Param("warehouseId") String warehouseId,
                                 @Param("companyCode") String companyCode,
                                 @Param("plantId") String plantId,
                                 @Param("languageId") String languageId,
                                 @Param("refDocNumber") String refDocNumber,
                                 @Param("statusId") Long statusId,
                                 @Param("statusDescription") String statusDescription);

    @Query(value = "SELECT DATEDIFF(MINUTE, ib.PA_CTD_ON, :lDate) from tblputawayheader ib \n"
            + "where ib.pa_no = :putAwayNumber and ib.wh_id = :warehouseId and ib.c_id = :companyCode and ib.plant_Id = :plantId and ib.lang_Id = :languageId and ib.is_deleted = 0", nativeQuery = true)
    public String getleadtime(@Param("companyCode") String companyCode,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param(value = "putAwayNumber") String putAwayNumber,
                              @Param("lDate") Date lDate);

    @Query(value = "select sum(pa_cnf_qty) \n" +
            "from tblputawayline where c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and \n" +
            "wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and PRE_IB_NO = :preInboundNo and \n" +
            "itm_code = :itemCode and mfr_name = :manufacturerName and \n" +
            "is_deleted = 0 and ib_line_no = :lineNo \n"+
            "group by itm_code,mfr_name,pre_ib_no,ref_doc_no,ib_line_no,lang_id,wh_id,plant_id,c_id ",nativeQuery = true)
    public Double getPutawayCnfQuantity(@Param("companyCode") String companyCode,
                                        @Param("plantId") String plantId,
                                        @Param("languageId") String languageId,
                                        @Param("warehouseId") String warehouseId,
                                        @Param("refDocNumber") String refDocNumber,
                                        @Param("preInboundNo") String preInboundNo,
                                        @Param("itemCode") String itemCode,
                                        @Param("manufacturerName") String manufacturerName,
                                        @Param("lineNo") Long lineNo);

    @Transactional
    @Procedure(procedureName = "putawayline_status_update_ib_cnf_proc")
    public void updatePutawayLineStatusUpdateInboundConfirmProc(
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
    @Procedure(procedureName = "ibheader_pal_cnt_update_proc")
    public void updateInboundHeaderRxdLinesCountProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo
    );
}