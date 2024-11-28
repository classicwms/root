package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PutAwayHeaderRepository extends JpaRepository<PutAwayHeader, Long>, JpaSpecificationExecutor<PutAwayHeader>,
        StreamableJpaSpecificationRepository<PutAwayHeader> {

    public List<PutAwayHeader> findAll();

    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param putAwayNumber
     * @param proposedStorageBin
     * @param deletionIndicator
     * @return
     */
    public Optional<PutAwayHeader>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndPutAwayNumberAndProposedStorageBinAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preInboundNo,
            String refDocNumber, String goodsReceiptNo, String palletCode, String caseCode, String packBarcodes,
            String putAwayNumber, String proposedStorageBin, Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param putAwayNumber
     * @param deletionIndicator
     * @return
     */
    public List<PutAwayHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
            String refDocNumber, String putAwayNumber, Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param deletionIndicator
     * @return
     */
    public List<PutAwayHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
            String refDocNumber, Long deletionIndicator);

    /**
     * @param companyId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    @Query(value = "SELECT COUNT(*) FROM tblputawayheader WHERE LANG_ID ='EN' AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (20, 22) AND IS_DELETED = 0", nativeQuery = true)
    public long getPutawayHeaderCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("preInboundNo") String preInboundNo,
            @Param("refDocNumber") String refDocNumber);

    @Query(value = "SELECT COUNT(*) FROM tblputawayheader WHERE LANG_ID ='EN' AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND REF_FIELD_5 = :itemCode AND MFR_NAME = :manufacturerName AND REF_FIELD_9 = :inboundLineNumber AND IS_DELETED = 0 AND STATUS_ID = 19", nativeQuery = true)
    public long getPutawayHeaderForInboundConfirm(@Param("companyId") String companyId,
                                                  @Param("plantId") String plantId,
                                                  @Param("warehouseId") String warehouseId,
                                                  @Param("preInboundNo") String preInboundNo,
                                                  @Param("refDocNumber") String refDocNumber,
                                                  @Param("itemCode") String itemCode,
                                                  @Param("manufacturerName") String manufacturerName,
                                                  @Param("inboundLineNumber") Long inboundLineNumber);

    // Count PutAwayHeader for MobileDashBoard
    @Query(value = "select count(*) from (SELECT COUNT(ref_doc_no) AS count FROM tblputawayheader WHERE "
            + "(:companyCode IS NULL OR C_ID IN (:companyCode)) AND "
            + "(:plantId IS NULL OR PLANT_ID IN (:plantId)) AND "
            + "(:warehouseId IS NULL OR WH_ID IN (:warehouseId)) AND "
            + "(:languageId IS NULL OR LANG_ID IN (:languageId)) AND "
            + "(STATUS_ID IN (:statusId)) AND "
            + "(IB_ORD_TYP_ID IN (:orderTypeId)) AND "
            + "IS_DELETED = 0 group by pack_barcode) x ", nativeQuery = true)
    public Long getPutAwayHeaderCount(
            @Param("companyCode") List<String> companyCode,
            @Param("plantId") List<String> plantId,
            @Param("warehouseId") List<String> warehouseId,
            @Param("languageId") List<String> languageId,
            @Param("statusId") Long statusId,
            @Param("orderTypeId") List<Long> orderTypeId);



    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param refDocNumber
     * @param statusId
     * @param deletionIndicator
     * @return
     */
    public List<PutAwayHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, List<Long> statusId, Long deletionIndicator);


    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param refDocNumber
     * @param packBarcodes
     * @param deletionIndicator
     * @return
     */
    public List<PutAwayHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, String packBarcodes, Long deletionIndicator);

    /**
     * @param warehouseId
     * @param statusId
     * @param orderTypeId
     * @return
     */
    public List<PutAwayHeader> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndInboundOrderTypeIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long statusId, List<Long> orderTypeId, Long deletionIndicator);
}