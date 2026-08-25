package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.palletizationlevelid.PalletizationLevelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface PalletizationLevelIdRepository extends JpaRepository<PalletizationLevelId,Long>, JpaSpecificationExecutor<PalletizationLevelId> {
	
	public List<PalletizationLevelId> findAll();
	public Optional<PalletizationLevelId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPalletizationLevelIdAndPalletizationLevelAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String palletizationLevelId, String palletizationLevel,String languageId, Long deletionIndicator);
}