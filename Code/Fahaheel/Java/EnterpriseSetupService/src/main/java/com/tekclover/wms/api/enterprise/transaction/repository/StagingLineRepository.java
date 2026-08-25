package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.staging.StagingLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface StagingLineRepository extends JpaRepository<StagingLineEntity, Long>,
        JpaSpecificationExecutor<StagingLineEntity> {

    /**
     *
     */
    public List<StagingLineEntity> findAll();

    /**
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param palletCode
     * @param caseCode
     * @param lineNo
     * @param itemCode
     * @param deletionIndicator
     * @return
     */
    public Optional<StagingLineEntity>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String palletCode, String caseCode, Long lineNo, String itemCode, Long deletionIndicator);

    // WH_ID/PRE_IB_NO/REF_DOC_NO/STG_NO/IB_LINE_NO/ITM_CODE/CASE_CODE
    public List<StagingLineEntity>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String stagingNo, Long lineNo, String itemCode, String caseCode,
            Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @param statusIds
     * @return
     */
    public List<StagingLineEntity> findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndLineNoAndItemCodeAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String preInboundNo, Long lineNo, String itemCode,
            List<Long> statusIds, Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @return
     */
    public List<StagingLineEntity>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
            String refDocNumber, Long lineNo, String itemCode, Long deletionIndicator);

    @Query(value = "SELECT COUNT(*) FROM tblstagingline WHERE LANG_ID ='EN' AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (14, 17) AND IS_DELETED = 0", nativeQuery = true)
    public long getStagingLineCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("preInboundNo") String preInboundNo,
            @Param("refDocNumber") String refDocNumber);

    public List<StagingLineEntity> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, Long lineNo, String itemCode, String caseCode, long l);

}