package com.tekclover.wms.api.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLinePartialConfirm;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface InboundLinePartialConfirmRepository extends JpaRepository<InboundLinePartialConfirm, Long>,
        JpaSpecificationExecutor<InboundLinePartialConfirm>, StreamableJpaSpecificationRepository<InboundLinePartialConfirm> {

	public List<InboundLinePartialConfirm> findByStatusIdAndIsExecuted (Long statusId, Long isExecuted);
	
	@Modifying(clearAutomatically = true)
    @Query("UPDATE InboundLinePartialConfirm ib SET ib.isExecuted = :isExecuted \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber \n" +
    		"and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updateInboundLinePartialConfirmExecutedStatus(@Param("languageId") String languageId,
						            @Param("plantId") String plantId,
						            @Param("companyCode") String companyCode,
						            @Param("warehouseId") String warehouseId,
	                                @Param("refDocNumber") String refDocNumber,
	                                @Param("isExecuted") Long isExecuted);   
}

