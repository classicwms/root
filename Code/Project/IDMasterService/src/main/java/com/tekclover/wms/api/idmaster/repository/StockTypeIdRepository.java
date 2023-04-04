package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.stocktypeid.StockTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface StockTypeIdRepository extends JpaRepository<StockTypeId,Long>, JpaSpecificationExecutor<StockTypeId> {
	
	public List<StockTypeId> findAll();
	public Optional<StockTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStockTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String stockTypeId, String languageId, Long deletionIndicator);
}