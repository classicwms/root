package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.transaction.model.inbound.gr.GrHeader;

@Repository
@Transactional
public interface GrHeaderRepository extends JpaRepository<GrHeader, Long>, JpaSpecificationExecutor<GrHeader>,
        StreamableJpaSpecificationRepository<GrHeader> {

    public List<GrHeader> findAll();

    public Optional<GrHeader>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preInboundNo, String refDocNumber, String stagingNo, String goodsReceiptNo,
            String palletCode, String caseCode, Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param goodsReceiptNo
     * @param caseCode
     * @param refDocNumber
     * @param deletionIndicator
     * @return
     */
    public List<GrHeader> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndCaseCodeAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String goodsReceiptNo,
            String caseCode, String refDocNumber, Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param refDocNumber
     * @param warehouseId
     * @param preInboundNo
     * @param caseCode
     * @return
     */
    public List<GrHeader> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, String warehouseId,
            String preInboundNo, String caseCode, Long deletionIndicator);

    public List<GrHeader> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
            String refDocNumber, long l);

    /**
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE GrHeader ib SET ib.statusId = :statusId WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
    void updateGrHeaderStatus(@Param("warehouseId") String warehouseId,
                              @Param("refDocNumber") String refDocNumber, @Param("statusId") Long statusId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE GrHeader ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updateGrHeaderStatus(@Param("warehouseId") String warehouseId,
                              @Param("companyCode") String companyCode,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("statusId") Long statusId,
                              @Param("statusDescription") String statusDescription);


    @Query(value = "SELECT COUNT(*) AS count FROM tblgrheader WHERE "
            + "(:companyCode IS NULL OR c_id = :companyCode) AND "
            + "(:plantId IS NULL OR plant_id = :plantId) AND "
            + "(:warehouseId IS NULL OR wh_id = :warehouseId) AND "
            + "(:languageId IS NULL OR lang_id = :languageId) AND "
            + "(:statusId IS NULL OR status_id IN (:statusId)) AND "
            + "is_deleted = 0", nativeQuery = true)
    Long grHeaderCount(@Param("companyCode") List<String> companyCode,
                       @Param("plantId") List<String> plantId,
                       @Param("languageId") List<String> languageId,
                       @Param("warehouseId") List<String> warehouseId,
                       @Param("statusId") Long statusId);


    List<GrHeader> findByWarehouseIdAndStatusIdAndDeletionIndicator(String warehouseId, Long statusId ,Long deletionIndicator);

    List<GrHeader> findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndStatusIdInAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId,
            List<Long> statusId, Long deletionIndicator);



}