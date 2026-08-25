package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.dto.ImPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ImPartnerRepository extends JpaRepository<ImPartner,Long>, JpaSpecificationExecutor<ImPartner> {


	List<ImPartner> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerItemBarcodeAndItemCodeAndManufacturerNameAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId,
			String partnerItemBarcode, String itemCode, String manufacturerName, Long deletionIndicator);

	List<ImPartner> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerItemBarcodeAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, String partnerItemBarcode, Long deletionIndicator);

	List<ImPartner> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);

	List<ImPartner> findAllByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
			String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode, String manufacturerName, Long deletionIndicator);

    List<ImPartner> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPartnerItemBarcodeAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String barcodeId, Long deletionIndicator);
}