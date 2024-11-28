package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.dock.Dock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface DockRepository extends JpaRepository<Dock, String>, JpaSpecificationExecutor<Dock> {

    Optional<Dock> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDockTypeAndDockIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String dockType, String dockId, Long deletionIndicator);
}