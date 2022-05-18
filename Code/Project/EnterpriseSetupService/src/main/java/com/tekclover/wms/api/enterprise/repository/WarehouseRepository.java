package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.warehouse.Warehouse;

@Repository
@Transactional
public interface WarehouseRepository extends JpaRepository<Warehouse,Long>, JpaSpecificationExecutor<Warehouse> {

	public List<Warehouse> findAll();
	public Optional<Warehouse> findByWarehouseId(String warehouseId);
	
	public Optional<Warehouse> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndModeOfImplementationAndWarehouseTypeIdAndDeletionIndicator (
			String languageId, String companyId, String plantId, String warehouseId, String modeOfImplementation, 
			Long warehouseTypeId, Long deletionIndicator);
}