package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
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
public interface PreInboundHeaderV2Repository extends JpaRepository<PreInboundHeaderEntityV2, Long>,
        JpaSpecificationExecutor<PreInboundHeaderEntityV2>, StreamableJpaSpecificationRepository<PreInboundHeaderEntityV2> {

    public List<PreInboundHeaderEntityV2> findAll();

    public Optional<PreInboundHeaderEntityV2> findByPreInboundNoAndWarehouseIdAndDeletionIndicator(
            String preInboundNo, String warehouseId, Long deletionIndicator);

    boolean existsByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(String refDocNumber, Long inboundOrderTypeId, Long deletionIndicator);


    public PreInboundHeaderEntityV2 findByWarehouseId(String warehouseId);

    public Optional<PreInboundHeaderEntityV2>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, Long deletionIndicator);

    // Pass WH_ID in PREINBOUNDHEADER table and fetch the Count of values where STATUS_ID=06,07 and Autopopulate
    public long countByWarehouseIdAndStatusIdIn(String warehouseId, List<Long> statusId);


    public List<PreInboundHeaderEntityV2> findByWarehouseIdAndStatusIdAndDeletionIndicator(
            String warehouseId, Long statusId, Long deletionIndicator);

    public Optional<PreInboundHeaderEntityV2> findByWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String warehouseId, String preInboundNo, String refDocNumner, Long deletionIndicator);

    public Optional<PreInboundHeaderEntityV2> findByPreInboundNoAndDeletionIndicator(String preInboundNo, Long deletionIndicator);

    public Optional<PreInboundHeaderEntityV2> findByRefDocNumberAndDeletionIndicator(String refDocNumber, Long deletionIndicator);
    public Optional<PreInboundHeaderEntityV2> findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(String refDocNumber, Long inboundOrderTypeId, Long deletionIndicator);

    @Query(value = "Select REF_DOC_TYP from tblpreinboundheader where WH_ID = :warehouseId and ref_doc_no = :refDocNumber \n" +
            " and PRE_IB_NO = :preInboundNo and IS_DELETED = :delete ", nativeQuery = true)
    public String getReferenceDocumentTypeFromPreInboundHeader(@Param("warehouseId") String warehouseId,
                                                               @Param("preInboundNo") String preInboundNo,
                                                               @Param("refDocNumber") String refDocNumber,
                                                               @Param("delete") Long delete);

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreInboundHeaderEntityV2 ib SET ib.statusId = :statusId WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
    void updatePreInboundHeaderEntityV2Status(@Param("warehouseId") String warehouseId,
                                              @Param("refDocNumber") String refDocNumber,
                                              @Param("statusId") Long statusId);

    Optional<PreInboundHeaderEntityV2> findByPreInboundNoAndWarehouseIdAndCompanyCodeAndPlantIdAndLanguageIdAndDeletionIndicator(
            String preInboundNo, String warehouseId, String companyCode,
            String plantId, String languageId, Long deletionIndicator);

    List<PreInboundHeaderEntityV2> findByWarehouseIdAndCompanyCodeAndPlantIdAndLanguageIdAndStatusIdAndDeletionIndicator(
            String warehouseId, String companyCode, String plantId, String languageId,
            Long statusId, long deletionIndicator);

    Optional<PreInboundHeaderEntityV2> findByCompanyCodeAndPlantIdAndLanguageIdAndPreInboundNoAndWarehouseIdAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String preInboundNo, String warehouseId, Long deletionIndicator);

    Optional<PreInboundHeaderEntityV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumner, Long deletionIndicator);

    PreInboundHeaderEntityV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    PreInboundHeaderEntityV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreInboundHeaderEntityV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription " +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updatePreInboundHeaderEntityStatus(@Param("warehouseId") String warehouseId,
                                            @Param("companyCode") String companyCode,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("refDocNumber") String refDocNumber,
                                            @Param("statusId") Long statusId,
                                            @Param("statusDescription") String statusDescription);

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

    //BP
    @Query(value = "select * from tblpreinboundheader where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and " +
            " wh_id = :warehouseId and PRE_IB_NO = :preInboundNo and REF_DOC_NO = :refDocNumber and IS_DELETED = 0 ",nativeQuery = true)
    public PreInboundHeaderEntityV2 getPreInboundHeaderV6(@Param("companyCodeId") String companyCode,
                                                          @Param("plantId") String plantId,
                                                          @Param("languageId") String languageId,
                                                          @Param("warehouseId") String warehouseId,
                                                          @Param("preInboundNo") String preInboundNo,
                                                          @Param("refDocNumber") String refDocNumber);


    @Modifying
    @Query(value = "delete tblpreinboundheader where c_id = :companyCodeId and plant_id = :plantId and " +
            " lang_id = :languageId and wh_id = :warehouseId and ref_doc_no = :refDocNumber ",nativeQuery = true)
    void deletePreInboundHeaderV4(@Param("companyCodeId") String companyCode,
                                  @Param("plantId") String plantId,
                                  @Param("languageId") String languageId,
                                  @Param("warehouseId") String warehouseId,
                                  @Param("refDocNumber") String refDocNumber);

    @Modifying
    @Query(value = "delete tblpreinboundline where c_id = :companyCodeId and plant_id = :plantId and " +
            " lang_id = :languageId and wh_id = :warehouseId and ref_doc_no = :refDocNumber ",nativeQuery = true)
    void deletePreInboundLineV4(@Param("companyCodeId") String companyCode,
                                @Param("plantId") String plantId,
                                @Param("languageId") String languageId,
                                @Param("warehouseId") String warehouseId,
                                @Param("refDocNumber") String refDocNumber);

    @Modifying
    @Query(value = "delete tblinboundheader where c_id = :companyCodeId and plant_id = :plantId and " +
            " lang_id = :languageId and wh_id = :warehouseId and ref_doc_no = :refDocNumber ",nativeQuery = true)
    void deleteInboundHeaderV4(@Param("companyCodeId") String companyCode,
                               @Param("plantId") String plantId,
                               @Param("languageId") String languageId,
                               @Param("warehouseId") String warehouseId,
                               @Param("refDocNumber") String refDocNumber);

    @Modifying
    @Query(value = "delete tblinboundline where c_id = :companyCodeId and plant_id = :plantId and " +
            " lang_id = :languageId and wh_id = :warehouseId and ref_doc_no = :refDocNumber ",nativeQuery = true)
    void deleteInboundLineV4(@Param("companyCodeId") String companyCode,
                             @Param("plantId") String plantId,
                             @Param("languageId") String languageId,
                             @Param("warehouseId") String warehouseId,
                             @Param("refDocNumber") String refDocNumber);

    @Modifying
    @Query(value = "delete tblstagingheader where c_id = :companyCodeId and plant_id = :plantId and " +
            " lang_id = :languageId and wh_id = :warehouseId and ref_doc_no = :refDocNumber ",nativeQuery = true)
    void deleteStagingHeaderV4(@Param("companyCodeId") String companyCode,
                               @Param("plantId") String plantId,
                               @Param("languageId") String languageId,
                               @Param("warehouseId") String warehouseId,
                               @Param("refDocNumber") String refDocNumber);

    @Modifying
    @Query(value = "delete tblstagingline where c_id = :companyCodeId and plant_id = :plantId and " +
            " lang_id = :languageId and wh_id = :warehouseId and ref_doc_no = :refDocNumber ",nativeQuery = true)
    void deleteStagingLineV4(@Param("companyCodeId") String companyCode,
                             @Param("plantId") String plantId,
                             @Param("languageId") String languageId,
                             @Param("warehouseId") String warehouseId,
                             @Param("refDocNumber") String refDocNumber);

    @Modifying
    @Query(value = "delete tblgrheader where c_id = :companyCodeId and plant_id = :plantId and " +
            " lang_id = :languageId and wh_id = :warehouseId and ref_doc_no = :refDocNumber ",nativeQuery = true)
    void deleteGrHeaderV4(@Param("companyCodeId") String companyCode,
                          @Param("plantId") String plantId,
                          @Param("languageId") String languageId,
                          @Param("warehouseId") String warehouseId,
                          @Param("refDocNumber") String refDocNumber);

    @Modifying
    @Query(value = "delete tblgrline where c_id = :companyCodeId and plant_id = :plantId and " +
            " lang_id = :languageId and wh_id = :warehouseId and ref_doc_no = :refDocNumber ",nativeQuery = true)
    void deleteGrLineV4(@Param("companyCodeId") String companyCode,
                        @Param("plantId") String plantId,
                        @Param("languageId") String languageId,
                        @Param("warehouseId") String warehouseId,
                        @Param("refDocNumber") String refDocNumber);

    @Modifying
    @Query(value = "delete tblputawayheader where c_id = :companyCodeId and plant_id = :plantId and " +
            " lang_id = :languageId and wh_id = :warehouseId and ref_doc_no = :refDocNumber ",nativeQuery = true)
    void deletePutawayHeaderV4(@Param("companyCodeId") String companyCode,
                               @Param("plantId") String plantId,
                               @Param("languageId") String languageId,
                               @Param("warehouseId") String warehouseId,
                               @Param("refDocNumber") String refDocNumber);
}