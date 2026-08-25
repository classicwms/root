package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.warehouse.WarehouseId;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface WarehouseIdRepository extends JpaRepository<WarehouseId, Long>,
        JpaSpecificationExecutor<WarehouseId>, StreamableJpaSpecificationRepository<WarehouseId> {

    public List<WarehouseId> findAll();

    public Optional<WarehouseId> findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
            String toCompanyCode, String toBranchCode, String languageId, Long deletionIndicator);
}