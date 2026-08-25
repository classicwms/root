package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.sublevelid.SubLevelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface SubLevelIdRepository extends JpaRepository<SubLevelId,Long>, JpaSpecificationExecutor<SubLevelId> {
	
	public List<SubLevelId> findAll();
	public Optional<SubLevelId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndSubLevelIdAndLevelIdAndSubLevelAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String subLevelId,String levelId,String subLevel, String languageId, Long deletionIndicator);

    Optional<SubLevelId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndSubLevelIdAndLevelIdAndLanguageIdAndDeletionIndicator(
			String companyCode, String plantId, String warehouseId, String subLevelId, Long levelId, String languageId, Long deletionIndicator);
}