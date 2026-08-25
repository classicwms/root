package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.cyclecount.periodic.PeriodicHeader;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PeriodicHeaderRepository extends JpaRepository<PeriodicHeader, Long>,
        JpaSpecificationExecutor<PeriodicHeader>, StreamableJpaSpecificationRepository<PeriodicHeader> {

    public PeriodicHeader findByCompanyCodeAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNo(
            String companyCode, String plantId, String warehouseId, Long cycleCountTypeId, String cycleCountNo);

    public PeriodicHeader findByCycleCountNo(String cycleCountNo);
}