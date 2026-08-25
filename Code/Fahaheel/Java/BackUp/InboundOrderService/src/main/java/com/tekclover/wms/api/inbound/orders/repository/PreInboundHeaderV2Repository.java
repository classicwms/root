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

    boolean existsByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(String refDocNumber, Long inboundOrderTypeId, Long deletionIndicator);


    public Optional<PreInboundHeaderEntityV2> findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(String refDocNumber, Long inboundOrderTypeId, Long deletionIndicator);

    Optional<PreInboundHeaderEntityV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumner, Long deletionIndicator);


    @Modifying
    @Query(value = "UPDATE tblpreinboundheader SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, \n" +
            "UTD_BY = :updatedBy, UTD_ON = :updatedOn \n" +
            "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId AND PLANT_ID = :plantId \n" +
            "AND LANG_ID = :languageId AND WH_ID = :warehouseId \n" +
            "AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo", nativeQuery = true)
    void updatePreInboundHeader(@Param("statusId") Long statusId, @Param("statusDescription") String statusDescription,
                                @Param("updatedBy") String updatedBy, @Param("updatedOn") Date updatedOn,
                                @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId,
                                @Param("languageId") String languageId, @Param("warehouseId") String warehouseId,
                                @Param("refDocNumber") String refDocNumber, @Param("preInboundNo") String preInboundNo);
}