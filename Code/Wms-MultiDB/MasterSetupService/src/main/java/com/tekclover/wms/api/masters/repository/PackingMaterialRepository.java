package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.packingmaterial.PackingMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface PackingMaterialRepository extends JpaRepository<PackingMaterial, Long>, JpaSpecificationExecutor<PackingMaterial> {

    Optional<PackingMaterial> findByPackingMaterialNoAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(String packingMaterialNo, String companyCodeId, String plantId, String warehouseId, String languageId, Long deletionIndicator);
}