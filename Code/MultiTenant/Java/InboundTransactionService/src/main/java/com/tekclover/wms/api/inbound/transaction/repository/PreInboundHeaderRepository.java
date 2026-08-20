package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.PreInboundHeaderEntity;

@Repository
@Transactional
public interface PreInboundHeaderRepository extends JpaRepository<PreInboundHeaderEntity, Long>,
        JpaSpecificationExecutor<PreInboundHeaderEntity>, StreamableJpaSpecificationRepository<PreInboundHeaderEntity> {

    public List<PreInboundHeaderEntity> findAll();

    public Optional<PreInboundHeaderEntity> findByPreInboundNoAndWarehouseIdAndDeletionIndicator(String preInboundNo,
                                                                                                 String warehouseId, Long deletionIndicator);

    public PreInboundHeaderEntity findByWarehouseId(String warehouseId);

    public Optional<PreInboundHeaderEntity>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber, Long deletionIndicator);

    // Pass WH_ID in PREINBOUNDHEADER table and fetch the Count of values where STATUS_ID=06,07 and Autopopulate
    public long countByWarehouseIdAndStatusIdIn(String warehouseId, List<Long> statusId);


    public List<PreInboundHeaderEntity> findByWarehouseIdAndStatusIdAndDeletionIndicator(String warehouseId, Long statusId, Long deletionIndicator);

    public Optional<PreInboundHeaderEntity> findByWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String warehouseId, String preInboundNo, String refDocNumner, Long deletionIndicator);

    public Optional<PreInboundHeaderEntity> findByPreInboundNoAndDeletionIndicator(String preInboundNo, long l);

    public Optional<PreInboundHeaderEntity> findByRefDocNumberAndDeletionIndicator(String refDocNumber, long l);

    @Query(value = "Select REF_DOC_TYP from tblpreinboundheader where WH_ID = :warehouseId and ref_doc_no = :refDocNumber \n" +
            " and PRE_IB_NO = :preInboundNo and IS_DELETED = :delete ", nativeQuery = true)
    public String getReferenceDocumentTypeFromPreInboundHeader(@Param("warehouseId") String warehouseId,
                                                               @Param("preInboundNo") String preInboundNo,
                                                               @Param("refDocNumber") String refDocNumber,
                                                               @Param("delete") Long delete);

    /**
     *
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreInboundHeaderEntity ib SET ib.statusId = :statusId WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
    void updatePreInboundHeaderEntityStatus(@Param("warehouseId") String warehouseId,
                                            @Param("refDocNumber") String refDocNumber,
                                            @Param("statusId") Long statusId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreInboundHeaderEntity ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription " +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updatePreInboundHeaderEntityStatus(@Param("warehouseId") String warehouseId,
                                            @Param("companyCode") String companyCode,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("refDocNumber") String refDocNumber,
                                            @Param("statusId") Long statusId,
                                            @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblpreinboundheader\r\n"
            + "	SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, \r\n"
            + "	UTD_BY = :updatedBy, UTD_ON = :updatedOn\r\n"
            + "	WHERE IS_DELETED = 0 AND \r\n"
            + "			C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \r\n"
            + "			REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo", nativeQuery = true)
    void updatePreInboundHeaderStatusOnPartialConfirmationV10(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    void deleteByCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Transactional
    @Procedure(procedureName = "pibheader_status_update_ib_cnf_proc")
    public void updatePreIbheaderStatusUpdateInboundConfirmProc(
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

    PreInboundHeaderEntity findByRefDocNumberAndPreInboundNo(String refDocNumber,String preInboundNo);


    @Modifying(clearAutomatically = true)
    @Query(value = "update tblpreinboundheader set is_deleted = 1 where REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo ",nativeQuery = true)
    void softDeleteByRefDocNo(@Param("refDocNumber") String refDocNumber,
                              @Param("preInboundNo") String preInboundNo);
}