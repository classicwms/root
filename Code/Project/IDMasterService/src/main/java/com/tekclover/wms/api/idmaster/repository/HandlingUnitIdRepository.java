package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.handlingunitid.HandlingUnitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface HandlingUnitIdRepository extends JpaRepository<HandlingUnitId,Long>, JpaSpecificationExecutor<HandlingUnitId> {
	
	public List<HandlingUnitId> findAll();
	public Optional<HandlingUnitId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingUnitIdAndHandlingUnitNumberAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String handlingUnitId,String handlingUnitNumber, String languageId, Long deletionIndicator);
}