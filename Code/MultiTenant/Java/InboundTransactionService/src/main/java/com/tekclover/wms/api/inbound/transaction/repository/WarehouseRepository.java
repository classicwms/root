package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>,
        JpaSpecificationExecutor<Warehouse>, StreamableJpaSpecificationRepository<Warehouse> {

    public List<Warehouse> findAll();

    public Optional<Warehouse> findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
            String toCompanyCode, String toBranchCode, String languageId, Long deletionIndicator);

    Warehouse findByWarehouseIdAndDeletionIndicator(
            String warehouseId, Long deletionIndicator
    );

    Warehouse findTop1ByWarehouseIdAndDeletionIndicator(
            String warehouseId, Long deletionIndicator
    );
}