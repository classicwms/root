package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.PutAwayLine;

@Repository
@Transactional
public interface PutAwayLineRepository extends JpaRepository<PutAwayLine, Long>,
        JpaSpecificationExecutor<PutAwayLine>, StreamableJpaSpecificationRepository<PutAwayLine> {

    /**
     *
     */
    public List<PutAwayLine> findAll();

    /**
     * @param languageId
     * @param CompanyCode
     * @param plantId
     * @param warehouseId
     * @param goodsReceiptNo
     * @param preInboundNo
     * @param refDocNumber
     * @param putAwayNumber
     * @param lineNo
     * @param itemCode
     * @param proposedStorageBin
     * @param confirmedStorageBin
     * @param deletionIndicator
     * @return
     */
    public Optional<PutAwayLine>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
            String languageId, String CompanyCode, String plantId, String warehouseId, String goodsReceiptNo, String preInboundNo, String refDocNumber, String putAwayNumber, Long lineNo, String itemCode,
            String proposedStorageBin, List<String> confirmedStorageBin, Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param deletionIndicator
     * @return
     */
    public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber,
            Long lineNo, String itemCode, Long deletionIndicator);

    public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber,
            List<Long> statusIds, Long deletionIndicator);

    /**
     * @param companyId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    @Query(value = "SELECT COUNT(*) FROM tblputawayline WHERE LANG_ID ='EN' AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (20, 22) AND IS_DELETED = 0", nativeQuery = true)
    public long getPutawayLineCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
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
    @Query(value = "SELECT COUNT(*) FROM tblputawayline WHERE LANG_ID ='EN' AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PA_NO = :putAwayNumber AND REF_DOC_NO = :refDocNumber AND STATUS_ID = :statusId AND IS_DELETED = 0", nativeQuery = true)
    public long getPutawayLineCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("putAwayNumber") String putAwayNumber,
            @Param("refDocNumber") String refDocNumber,
            @Param("statusId") Long statusId);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param refDocNumber
     * @return
     */
    public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, Long deletionIndicator);

    /**
     * @param languageId
     * @param plantId
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String CompanyCode, String plantId, String refDocNumber, String packBarcodes, Long deletionIndicator);

    public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String putAwayNumber, Long deletionIndicator);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = "SELECT SUM(PA_CNF_QTY) AS SUM_PAQTY_VALUE FROM tblputawayline \r\n"
            + " WHERE ITM_CODE IN :itemCode AND STATUS_ID = 20 AND IS_DELETED = 0 \r\n"
            + " AND PA_CTD_ON BETWEEN :dateFrom AND :dateTo GROUP BY ITM_CODE", nativeQuery = true)
    public Double findSumOfPAConfirmQty(@Param(value = "itemCode") List<String> itemCode,
                                        @Param(value = "dateFrom") Date dateFrom,
                                        @Param(value = "dateTo") Date dateTo);

	public List<PutAwayLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
			String refDocNumber, long l);
	
	@Modifying(clearAutomatically = true)
    @Query("UPDATE PutAwayLine ib SET ib.statusId = :statusId \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updatePutawayLineStatus(@Param("warehouseId") String warehouseId,
                                    @Param("companyCode") String companyCode,
                                    @Param("plantId") String plantId,
                                    @Param("languageId") String languageId,
                                    @Param("refDocNumber") String refDocNumber,
                                    @Param("statusId") Long statusId);
}