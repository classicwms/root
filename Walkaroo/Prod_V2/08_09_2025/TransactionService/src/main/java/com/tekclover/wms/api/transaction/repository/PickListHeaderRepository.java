package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.outbound.v2.PickListHeader;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional
public interface PickListHeaderRepository extends JpaRepository<PickListHeader, Long>,
        JpaSpecificationExecutor<PickListHeader>,
        StreamableJpaSpecificationRepository<PickListHeader> {

    @Transactional
    @Procedure(procedureName = "pick_list_cnf_update_proc")
    public void updatePickupLineQualityHeaderLineCnfByUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("oldRefDocNumber") String oldRefDocNumber,
            @Param("oldPreOutboundNo") String oldPreOutboundNo,
            @Param("newRefDocNumber") String newRefDocNumber,
            @Param("newPreOutboundNo") String newPreOutboundNo,
            @Param("salesOrderNo") String salesOrderNo);

    @Transactional
    @Procedure(procedureName = "pick_list_delete_proc")
    void updateDeletionIndicatorPickListCancellationProc(@Param("companyCodeId") String companyCodeId,
                                                         @Param("plantId") String plantId,
                                                         @Param("languageId") String languageId,
                                                         @Param("warehouseId") String warehouseId,
                                                         @Param("refDocNumber") String refDocNumber,
                                                         @Param("preOutboundNo") String preOutboundNo,
                                                         @Param("updatedBy") String updatedBy,
                                                         @Param("updatedOn") Date updatedOn);

}