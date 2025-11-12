package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface PreOutboundLineV2Repository extends JpaRepository<PreOutboundLineV2, Long>,
        JpaSpecificationExecutor<PreOutboundLineV2>, StreamableJpaSpecificationRepository<PreOutboundLineV2> {


    Optional<PreOutboundLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber,
            String preOutboundNo, String partnerCode, Long lineNumber, String itemCode, Long deletionIndicator);

    List<PreOutboundLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String refDocNumber, String preOutboundNo, String partnerCode, Long deletionIndicator);

    List<PreOutboundLineV2> findByPreOutboundNo(String preOutboundNo);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreOutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \n" +
            "WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId \n" +
            "AND ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo")
    void updatePreOutboundLineStatusV2(@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("refDocNumber") String refDocNumber,
                                       @Param("preOutboundNo") String preOutboundNo,
                                       @Param("statusId") Long statusId,
                                       @Param("statusDescription") String statusDescription);

    @Query(value=" SELECT SUM(ORD_QTY) FROM tblpreoutboundline\r\n"
            + " WHERE REF_DOC_NO = :refDocNumber \r\n"
            + " GROUP BY REF_DOC_NO", nativeQuery=true)
    public Long getSumofOrderedQty(@Param ("refDocNumber") String refDocNumber);

    @Query(value=" SELECT COUNT(ORD_QTY) FROM tblpreoutboundline\r\n"
            + " WHERE REF_DOC_NO = :refDocNumber \r\n"
            + " GROUP BY REF_DOC_NO", nativeQuery=true)
    public Long getCountOfOrderedQty(@Param ("refDocNumber") String refDocNumber);

//    @Query(value = "SELECT COUNT(*) FROM tblpickupline \r\n "
//    + "WHERE REF_DOC_NO = :refDocNumber \r\n"
//    + "GROUP BY REF_DOC_NO", nativeQuery = true)
//    public Long getCountOfLine(@Param("refDocNumber")String refDocNumber);

    @Query(value = "SELECT COUNT(*) FROM tblpickupline WHERE REF_DOC_NO = :refDocNumber AND STATUS_ID = 50 AND IS_DELETED = 0 ", nativeQuery = true)
    public Long getCountOfLine(@Param("refDocNumber") String refDocNumber);

    @Query(value = "SELECT SUM(PICK_CNF_QTY) FROM tblpickupline WHERE REF_DOC_NO = :refDocNumber", nativeQuery = true)
    public Long getCountOfPickedQty(@Param("refDocNumber") String refDocNumber);

    List<PreOutboundLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

    List<PreOutboundLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<PreOutboundLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

    //=========================================PGIReversal=========================================================
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreOutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \n" +
            "WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId \n" +
            "AND ob.refDocNumber = :refDocNumber AND ob.itemCode = :itemCode")
    public void updatePreOutboundLineStatusV3 (@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("refDocNumber") String refDocNumber,
                                       @Param("itemCode") String itemCode,
                                       @Param("statusId") Long statusId,
                                       @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblpreoutboundline SET STATUS_ID = :statusId, REF_FIELD_10 = :statusDescription, STATUS_TEXT = :statusDescription \n" +
            "WHERE LANG_ID = :languageId AND C_ID = :companyCodeId AND \n" +
            "PLANT_ID = :plantId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber ", nativeQuery = true)
    void updatePreOutboundLineStatus(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("refDocNumber") String refDocNumber,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);
    
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblpreoutboundline SET is_deleted = 1 where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber AND pre_ob_no = :preOutboundNo " +
            "AND is_deleted = 0", nativeQuery = true)
    void deletePreOutboundLine(@Param("companyCodeId") String companyCodeId,
                                   @Param("plantId") String plantId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("preOutboundNo") String preOutboundNo);

    @Modifying
    @Query(value = "update tblpreoutboundline set SALES_ORDER_NUMBER = :salesOrderNo where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber " +
            "AND is_deleted = 0", nativeQuery = true)
    int updateSalesOrderNo(@Param("companyCodeId") String companyCodeId,
                            @Param("plantId") String plantId,
                            @Param("warehouseId") String warehouseId,
                            @Param("refDocNumber") String refDocNumber,
                            @Param("salesOrderNo") String salesOrderNo);

    @Modifying(clearAutomatically = true)
    @Query(value = "delete tblpreoutboundline where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber " +
            "AND is_deleted = 0", nativeQuery = true)
    void deletePreOutboundLine(@Param("companyCodeId") String companyCodeId,
                               @Param("plantId") String plantId,
                               @Param("warehouseId") String warehouseId,
                               @Param("refDocNumber") String refDocNumber);
}