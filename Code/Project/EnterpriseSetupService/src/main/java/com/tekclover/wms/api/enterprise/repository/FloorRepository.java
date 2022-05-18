package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.floor.Floor;

@Repository
@Transactional
public interface FloorRepository extends JpaRepository<Floor,Long>, JpaSpecificationExecutor<Floor> {
	public List<Floor> findAll();
	
	public Optional<Floor> findByFloorId(Long floorId);
	
	public Optional<Floor> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndFloorIdAndDeletionIndicator (
	String languageId, String companyId, String plantId, String warehouseId, Long floorId, Long deletionIndicator);
}