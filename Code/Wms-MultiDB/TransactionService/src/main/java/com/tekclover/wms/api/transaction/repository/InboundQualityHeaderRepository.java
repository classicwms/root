package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.inbound.inboundquality.InboundQualityHeader;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.Optional;

@Repository
@Transactional
public interface InboundQualityHeaderRepository extends JpaRepository<InboundQualityHeader, Long>,
        JpaSpecificationExecutor<InboundQualityHeader>, StreamableJpaSpecificationRepository<InboundQualityHeader> {


    Optional<InboundQualityHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndInboundQualityNumberAndItemCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String refDocNumber, String preInboundNo, String inboundQualityNumber, String itemCode, Long deletionIndicator);

    @Transactional
    @Procedure(procedureName = "ib_qh_status_update_proc")
    void updateQualityHeaderStatusProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("itemCode") String itemCode,
            @Param("inboundQualityNumber") String inboundQualityNumber,
            @Param("lineNumber") String lineNumber,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

}