package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.inbound.inboundquality.InboundQualityLine;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface InboundQualityLineRepository extends JpaRepository<InboundQualityLine, Long>,
        JpaSpecificationExecutor<InboundQualityLine>, StreamableJpaSpecificationRepository<InboundQualityLine> {


    List<InboundQualityLine> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndInboundQualityNumberAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String refDocNumber, String preInboundNo, String inboundQualityNumber, Long deletionIndicator);

    Optional<InboundQualityLine> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndInboundQualityNumberAndItemCodeAndLineNoAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, String inboundQualityNumber, String itemCode, Long lineNo, Long deletionIndicator);

}