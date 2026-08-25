package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.impl.PutAwayHeaderImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayHeaderV2;
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
public interface PutAwayHeaderV2Repository extends JpaRepository<PutAwayHeaderV2, Long>,
        JpaSpecificationExecutor<PutAwayHeaderV2>, StreamableJpaSpecificationRepository<PutAwayHeaderV2> {


    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String putAwayNumber, Long deletionIndicator);

    Optional<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndPutAwayNumberAndProposedStorageBinAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String goodsReceiptNo, String palletCode, String caseCode,
            String packBarcodes, String putAwayNumber, String proposedStorageBin, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, List<Long> statusIds, Long deletionIndicator);

    List<PutAwayHeaderV2> findByWarehouseIdAndStatusIdAndInboundOrderTypeIdInAndDeletionIndicator(
            String warehouseId, Long statusId, List<Long> orderTypeId, Long deletionIndicator);

    @Query(value = "SELECT * from tblputawayheader \n"
            + "where pa_no = :putAwayNumber and is_deleted = 0", nativeQuery = true)
    public PutAwayHeaderV2 getPutAwayHeaderV2(@Param(value = "putAwayNumber") String putAwayNumber);

    PutAwayHeaderV2 findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPutAwayNumberAndDeletionIndicator(
            String companyId, String plantId, String warehouseId, String languageId, String putAwayNumber, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String refDocNumber, String preInboundNumber, Long deletionIndicator);

    PutAwayHeaderV2 findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId,
            String preInboundNo, String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndManufacturerNameAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId,
            String itemCode, String manufacturerName, Long statusId, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId, Long statusId, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
            String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode, String manufacturerName, Long statusId, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
            String companyCodeId, String plantId, String warehouseId, String languageId, Long statusId, Long deletionIndicator);

    PutAwayHeaderV2 findByPutAwayNumberAndDeletionIndicator(String putAwayNumber, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, List<Long> statusIds, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, List<Long> statusIds, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Query(value = "SELECT COUNT(*) FROM tblputawayheader WHERE LANG_ID ='EN' AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (19) AND IS_DELETED = 0", nativeQuery = true)
    public long getPutawayHeaderCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("preInboundNo") String preInboundNo,
            @Param("refDocNumber") String refDocNumber);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PutAwayHeaderV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCodeId = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updatePutAwayHeaderStatus(@Param("warehouseId") String warehouseId,
                                   @Param("companyCode") String companyCode,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("statusId") Long statusId,
                                   @Param("statusDescription") String statusDescription);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String putawayNumber, Long deletionIndicator);

    PutAwayHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preInboundNo,
            String refDocNumber, String putAwayNumber, Long deletionIndicator);


    @Modifying
    @Query(value = " UPDATE PAH SET PAH.STATUS_ID = :statusId, \n " +
            "PAH.STATUS_TEXT = :statusDescription, \n" +
            "PAH.PA_CNF_BY = :updatedBy, \n" +
            "PAH.PA_CNF_ON = :updatedOn FROM tblputawayheader PAH " +
            "INNER JOIN \n" +
            "(SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_IB_NO, IB_LINE_NO, ITM_CODE, MFR_NAME \n" +
            "FROM tblinboundline \n" +
            "WHERE IS_DELETED = 0 \n" +
            "AND REF_FIELD_2 = 'TRUE' AND STATUS_ID = :statusId AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId \n" +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo) X ON PAH.C_ID = X.C_ID AND PAH.PLANT_ID = X.PLANT_ID \n" +
            "AND PAH.LANG_ID = X.LANG_ID AND PAH.WH_ID = X.WH_ID AND PAH.REF_DOC_NO = X.REF_DOC_NO AND PAH.PRE_IB_NO = X.PRE_IB_NO \n" +
            "AND PAH.REF_FIELD_5 = X.ITM_CODE AND PAH.MFR_NAME = X.MFR_NAME AND PAH.REF_FIELD_9 = X.IB_LINE_NO \n" +
            "AND PAH.IS_DELETED = 0 ", nativeQuery = true)
    void updatePutawayHeader(@Param("companyCodeId") String companyCodeId,
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