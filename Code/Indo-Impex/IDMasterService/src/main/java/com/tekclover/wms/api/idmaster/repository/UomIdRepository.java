package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.uomid.UomId;


@Repository
@Transactional
public interface UomIdRepository extends JpaRepository<UomId,Long>, JpaSpecificationExecutor<UomId> {
	
	public List<UomId> findAll();
	public Optional<UomId> 
		findByCompanyCodeIdAndUomIdAndWarehouseIdAndPlantIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String uomId,String warehouseId,String plantId,String languageId, Long deletionIndicator);
}