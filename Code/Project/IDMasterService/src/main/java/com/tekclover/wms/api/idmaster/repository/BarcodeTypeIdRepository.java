package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;


@Repository
@Transactional
public interface BarcodeTypeIdRepository extends JpaRepository<BarcodeTypeId,Long>, JpaSpecificationExecutor<BarcodeTypeId> {
	
	public List<BarcodeTypeId> findAll();
	public Optional<BarcodeTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long barcodeTypeId, String languageId, Long deletionIndicator);
}