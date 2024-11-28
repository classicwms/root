package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.BarcodeSubTypeId;


@Repository
@Transactional
public interface BarcodeSubTypeIdRepository extends JpaRepository<BarcodeSubTypeId,Long>, JpaSpecificationExecutor<BarcodeSubTypeId> {
	
	public List<BarcodeSubTypeId> findAll();
	public Optional<BarcodeSubTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeTypeIdAndBarcodeSubTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long barcodeTypeId, Long barcodeSubTypeId, String languageId, Long deletionIndicator);
}