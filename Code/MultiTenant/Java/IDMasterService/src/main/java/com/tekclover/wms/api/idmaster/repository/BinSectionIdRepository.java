package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.binsectionid.BinSectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface BinSectionIdRepository extends JpaRepository<BinSectionId,Long>, JpaSpecificationExecutor<BinSectionId> {
	
	public List<BinSectionId> findAll();
	public Optional<BinSectionId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBinSectionIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String binSectionId, String languageId, Long deletionIndicator);
}