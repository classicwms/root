package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound.PreOutboundHeader;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PreOutboundHeaderRepository extends JpaRepository<PreOutboundHeader, Long>,
        JpaSpecificationExecutor<PreOutboundHeader>, StreamableJpaSpecificationRepository<PreOutboundHeader> {
    String UPGRADE_SKIPLOCKED = "-2";

    public List<PreOutboundHeader> findAll();

    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @param deletionIndicator
     * @return
     */
    public Optional<PreOutboundHeader>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber,
            String preOutboundNo, String partnerCode, Long deletionIndicator);

    public PreOutboundHeader findByPreOutboundNo(String preOutboundNo);

    public Optional<PreOutboundHeader> findByRefDocNumberAndDeletionIndicator(String refDocumentNo, long l);

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PreOutboundHeader ib SET ib.statusId = :statusId, REF_FIELD_10 = :refField10 WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
    void updatePreOutboundHeaderStatus(@Param("warehouseId") String warehouseId,
                                       @Param("refDocNumber") String refDocNumber, @Param("statusId") Long statusId, @Param("refField10") String refField10);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = UPGRADE_SKIPLOCKED)})
    public Optional<PreOutboundHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, long l);
}