package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.inboundordertypeid.InboundOrderTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InboundOrderTypeIdRepository extends JpaRepository<InboundOrderTypeId,String>,JpaSpecificationExecutor<InboundOrderTypeId> {
    Optional<InboundOrderTypeId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInboundOrderTypeIdAndLanguageIdAndDeletionIndicator(
            String companyCode,
            String plantId,
            String warehouseId,
            String inboundOrderTypeId,
            String languageId,
            long l);

}
