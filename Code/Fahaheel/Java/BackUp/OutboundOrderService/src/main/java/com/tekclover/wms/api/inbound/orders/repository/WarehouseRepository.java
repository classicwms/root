package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
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
}