package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.PreOutboundLine;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PreOutboundLineRepository extends JpaRepository<PreOutboundLine, Long>,
        JpaSpecificationExecutor<PreOutboundLine>, StreamableJpaSpecificationRepository<PreOutboundLine> {

    public List<PreOutboundLine> findAll();

    public List<PreOutboundLine> findByPreOutboundNo(String preOutboundNo);

    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param deletionIndicator
     * @return
     */
    public Optional<PreOutboundLine>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode,
            Long lineNumber, String itemCode, Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @param l
     * @return
     */
    public List<PreOutboundLine> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber,
            String preOutboundNo, String partnerCode, long l);

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreOutboundLine ob SET ob.statusId = :statusId WHERE ob.warehouseId = :warehouseId AND ob.refDocNumber = :refDocNumber")
    void updatePreOutboundLineStatus(@Param("warehouseId") String warehouseId,
                                     @Param("refDocNumber") String refDocNumber, @Param("statusId") Long statusId);

    @Modifying
    @Query(value = "UPDATE tblpreoutboundline SET WEBHOOK_STATUS = :webhookStatus \n" +
            "WHERE REF_DOC_NO = :refDocNo AND IS_DELETED = 0", nativeQuery = true)
    void updateWebhookStatus(@Param("refDocNo") String refDocNo,
                             @Param("webhookStatus") Long webHookStatus);

}