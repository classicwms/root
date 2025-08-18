package com.tekclover.wms.api.inbound.orders.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PickupLineRepository extends JpaRepository<PickupLine, Long>,
        JpaSpecificationExecutor<PickupLine>, StreamableJpaSpecificationRepository<PickupLine> {

    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
    public List<PickupLine> findAll();

    public Optional<PickupLine> findByActualHeNo(String actualHeNo);

    public PickupLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, Long deletionIndicator);

    public PickupLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndActualHeNoAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String pickupNumber, String itemCode, String pickedStorageBin, String pickedPackCode, String actualHeNo,
            long l);

    public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, Long deletionIndicator);

    public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndPickedPackCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, String pickedPackCode, Long deletionIndicator);

    public PickupLine findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndActualHeNoAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String pickupNumber, String itemCode, String actualHeNo, String pickedStorageBin, String pickedPackCode, Long deletionIndicator);

    @Query(value = "SELECT SUM(PICK_CNF_QTY) FROM tblpickupline WHERE WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND\r\n"
            + "PRE_OB_NO = :preOutboundNo AND OB_LINE_NO = :obLineNumber AND ITM_CODE = :itemCode AND IS_DELETED = 0 \r\n"
            + "GROUP BY REF_DOC_NO", nativeQuery = true)
    public Double getPickupLineCount(@Param("warehouseId") String warehouseId,
                                     @Param("refDocNumber") String refDocNumber,
                                     @Param("preOutboundNo") String preOutboundNo,
                                     @Param("obLineNumber") Long obLineNumber,
                                     @Param("itemCode") String itemCode);

    public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> lineNumbers,
            List<String> itemCodes, long l);

    @Query(value = "SELECT COUNT(OB_LINE_NO) AS lineCount FROM tblpickupline \r\n"
            + "	WHERE WH_ID = :warehouseId AND REF_DOC_NO IN :refDocNumber AND PRE_OB_NO = :preOutboundNo\r\n"
            + "	AND STATUS_ID=50 AND IS_DELETED = 0 GROUP BY REF_DOC_NO", nativeQuery = true)
    public Double getCountByWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
            @Param("warehouseId") String warehouseId,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("refDocNumber") List<String> refDocNumber);

    public List<PickupLine> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoInAndRefDocNumberInAndPartnerCodeAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, List<String> preOutboundNo,
            List<String> refDocNumber, String partnerCode, Long statusId, long l);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = "SELECT SUM(PU_QTY) AS SUM_PUQTY_VALUE FROM tblpickupline \r\n"
            + " WHERE ITM_CODE IN :itemCode AND STATUS_ID = 50 AND IS_DELETED = 0 \r\n"
            + " AND PICK_CTD_ON BETWEEN :dateFrom AND :dateTo GROUP BY ITM_CODE", nativeQuery = true)
    public Double findSumOfPickupLineQty(@Param(value = "itemCode") List<String> itemCode,
                                         @Param(value = "dateFrom") Date dateFrom,
                                         @Param(value = "dateTo") Date dateTo);

    @Query(value = "select level_id as levelId from tblhhtuser where usr_id in (:userId) and " +
            "c_id in (:companyCode) and plant_id in (:plantId) and lang_id in (:languageId) and " +
            " wh_id in (:warehouseId) and is_deleted = 0 ", nativeQuery = true)
    public String getLevelId(@Param("companyCode")String companyCode,
            @Param("plantId")String plantId,
            @Param("languageId")String languageId,
            @Param("warehouseId")String warehouseId,
            @Param(value = "userId")String userId);

    @Query(value = "SELECT level_id AS levelId FROM tblhhtuser WHERE "
            + "(:userId IS NULL OR usr_id IN (:userId)) AND "
            + "(:companyCode IS NULL OR c_id IN (:companyCode)) AND "
            + "(:plantId IS NULL OR plant_id IN (:plantId)) AND "
            + "(:languageId IS NULL OR lang_id IN (:languageId)) AND "
            + "(:warehouseId IS NULL OR wh_id IN (:warehouseId)) AND "
            + "is_deleted = 0", nativeQuery = true)
    public String getLevelIdForUserId(@Param("companyCode") List<String> companyCode,
                                      @Param("plantId") List<String> plantId,
                                      @Param("languageId") List<String> languageId,
                                      @Param("warehouseId") List<String> warehouseId,
                                      @Param("userId") List<String> userId);


}