package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.handlingunit.HandlingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface HandlingUnitRepository extends JpaRepository<HandlingUnit, Long>, JpaSpecificationExecutor<HandlingUnit> {

//	Optional<HandlingUnit> findByHandlingUnit(String handlingUnit);

    Optional<HandlingUnit> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingUnitAndLanguageIdAndDeletionIndicator(
			String companyCodeId, String plantId, String warehouseId, String handlingUnit, String languageId, Long deletionIndicator);
}