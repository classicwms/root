package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.outbound.packing.PackingInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PackingInventoryRepository extends JpaRepository<PackingInventory, String>, JpaSpecificationExecutor<PackingInventory> {


    PackingInventory findByCompanyIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackingMaterialNoAndDeletionIndicator(
            String companyId, String plantId, String languageId, String warehouseId, String packingMaterialNo, Long deletionIndicator);

    PackingInventory findByPackingMaterialNoAndDeletionIndicator(String packingMaterialNo, Long deletionIndicator);
}
