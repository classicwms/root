package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.levelid.LevelId;


@Repository
@Transactional
public interface LevelIdRepository extends JpaRepository<LevelId,Long>, JpaSpecificationExecutor<LevelId> {
	
	public List<LevelId> findAll();
	public Optional<LevelId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLevelIdAndLanguageIdAndLevelAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long levelId, String languageId, String level, Long deletionIndicator);
}