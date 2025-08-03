package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.returntypeid.ReturnTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface ReturnTypeIdRepository extends JpaRepository<ReturnTypeId,Long>, JpaSpecificationExecutor<ReturnTypeId> {
	
	public List<ReturnTypeId> findAll();
	public Optional<ReturnTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndReturnTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String returnTypeId, String languageId, Long deletionIndicator);
}