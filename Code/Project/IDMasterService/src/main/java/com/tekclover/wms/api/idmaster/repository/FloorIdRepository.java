package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.floorid.FloorId;


@Repository
@Transactional
public interface FloorIdRepository extends JpaRepository<FloorId,Long>, JpaSpecificationExecutor<FloorId> {
	
	public List<FloorId> findAll();
	public Optional<FloorId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long floorId, String languageId, Long deletionIndicator);
}