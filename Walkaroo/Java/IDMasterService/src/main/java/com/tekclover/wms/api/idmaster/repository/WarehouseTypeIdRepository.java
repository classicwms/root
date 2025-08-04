package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.warehousetypeid.WarehouseTypeId;


@Repository
@Transactional
public interface WarehouseTypeIdRepository extends JpaRepository<WarehouseTypeId,Long>, JpaSpecificationExecutor<WarehouseTypeId> {
	
	public List<WarehouseTypeId> findAll();
	public Optional<WarehouseTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndWarehouseTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long warehouseTypeId, String languageId, Long deletionIndicator);
}