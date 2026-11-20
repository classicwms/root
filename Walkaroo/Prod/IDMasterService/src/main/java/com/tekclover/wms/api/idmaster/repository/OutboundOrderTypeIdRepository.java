package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.outboundordertypeid.OutboundOrderTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutboundOrderTypeIdRepository extends JpaRepository<OutboundOrderTypeId,String>, JpaSpecificationExecutor<OutboundOrderTypeId> {


    Optional<OutboundOrderTypeId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndOutboundOrderTypeIdAndLanguageIdAndDeletionIndicator(
            String companyCode,
            String plantId,
            String warehouseId,
            String outboundOrderTypeId,
            String languageId,
            long l);
}
