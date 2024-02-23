package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.SpecialStockIndicatorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface SpecialStockIndicatorIdRepository extends JpaRepository<SpecialStockIndicatorId,Long>, JpaSpecificationExecutor<SpecialStockIndicatorId> {
	
	public List<SpecialStockIndicatorId> findAll();
	public Optional<SpecialStockIndicatorId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStockTypeIdAndSpecialStockIndicatorIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String stockTypeId, String specialStockIndicatorId, String languageId, Long deletionIndicator);
}