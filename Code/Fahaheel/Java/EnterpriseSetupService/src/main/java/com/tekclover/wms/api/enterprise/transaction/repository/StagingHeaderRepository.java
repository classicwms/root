package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.staging.StagingHeader;
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
public interface StagingHeaderRepository extends JpaRepository<StagingHeader, Long>, JpaSpecificationExecutor<StagingHeader>,
        StreamableJpaSpecificationRepository<StagingHeader> {
    /**
     *
     */
    public List<StagingHeader> findAll();

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param stagingNo
     * @param deletionIndicator
     * @return
     */
    public Optional<StagingHeader>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndDeletionIndicator(
            String languageId,
            String companyCode,
            String plantId,
            String warehouseId,
            String preInboundNo,
            String refDocNumber,
            String stagingNo,
            Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param deletionIndicator
     * @return
     */
    public List<StagingHeader> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, Long deletionIndicator);

    /**
     * @param warehouseId
     * @param statusId
     * @param deletionIndicator
     * @return
     */
    public List<StagingHeader> findByWarehouseIdAndStatusIdAndDeletionIndicator(String warehouseId,
                                                                                Long statusId, Long deletionIndicator);

    /**
     *
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE StagingHeader ib SET ib.statusId = :statusId WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
    void updateStagingHeaderStatus(@Param("warehouseId") String warehouseId,
                                   @Param("refDocNumber") String refDocNumber, @Param("statusId") Long statusId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE StagingHeader ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updateStagingHeaderStatus(@Param("warehouseId") String warehouseId,
                                   @Param("companyCode") String companyCode,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("statusId") Long statusId,
                                   @Param("statusDescription") String statusDescription);
}