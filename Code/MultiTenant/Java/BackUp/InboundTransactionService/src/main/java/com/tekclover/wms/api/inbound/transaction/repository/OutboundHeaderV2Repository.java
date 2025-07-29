package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.v2.OutboundHeaderV2Stream;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Repository
@Transactional
public interface OutboundHeaderV2Repository extends JpaRepository<OutboundHeaderV2, Long>,
        JpaSpecificationExecutor<OutboundHeaderV2>,
        StreamableJpaSpecificationRepository<OutboundHeaderV2> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OutboundHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.preOutboundNo = :preOutboundNo")
    public void updateOutboundHeaderStatusV3(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("languageId") String languageId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("preOutboundNo") String preOutboundNo,
                                             @Param("statusId") Long statusId,
                                             @Param("statusDescription") String statusDescription);
}