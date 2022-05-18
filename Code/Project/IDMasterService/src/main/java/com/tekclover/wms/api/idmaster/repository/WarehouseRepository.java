package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;


@Repository
@Transactional
public interface WarehouseRepository extends JpaRepository<Warehouse,Long>, JpaSpecificationExecutor<Warehouse> {
	
	public List<Warehouse> findAll();
	
	/**
	 * 
	 * @param companyCodeId
	 * @param warehouseId
	 * @param languageId
	 * @param plantId
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<Warehouse> 
		findByCompanyCodeAndWarehouseIdAndLanguageIdAndPlantIdAndDeletionIndicator(
				String companyCode, String warehouseId, String languageId, String plantId, Long deletionIndicator);

	/**
	 * 
	 * @param warehouseId
	 * @param deletionIndicator
	 * @return
	 */
	public Warehouse findByWarehouseIdAndDeletionIndicator(String warehouseId, Long deletionIndicator);
}