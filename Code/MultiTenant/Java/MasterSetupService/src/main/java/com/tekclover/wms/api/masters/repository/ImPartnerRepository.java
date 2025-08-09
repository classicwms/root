package com.tekclover.wms.api.masters.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.impartner.ImPartner;

@Repository
@Transactional
public interface ImPartnerRepository extends JpaRepository<ImPartner,Long>,
		JpaSpecificationExecutor<ImPartner>,
		StreamableJpaSpecificationRepository<ImPartner> {

	Optional<ImPartner> findByBusinessPartnerCode(String businessPartnerCode);
	List<ImPartner> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndDeletionIndicator(
			 String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode, Long deletionIndicator );

List<ImPartner> findByBusinessPartnerCodeAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndBusinessPartnerTypeAndPartnerItemBarcodeAndBrandNameAndManufacturerCodeAndManufacturerNameAndDeletionIndicator(
			String businessPartnerCode, String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode, String businessPartnerType, String partnerItemBarcode,
			String brandName,String manufacturerCode,String manufacturerName, Long deletionIndicator );


	ImPartner findByBusinessPartnerCodeAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndBusinessPartnerTypeAndPartnerItemBarcodeAndManufacturerNameAndDeletionIndicator(
			String businessPartnerCode, String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode,
			String businessPartnerType, String partnerItemBarcode, String manufacturerName, Long deletionIndicator);


    List<ImPartner> findByPartnerItemBarcodeAndDeletionIndicator(String partnerItemBarcode, Long deletionIndicator);

	List<ImPartner> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPartnerItemBarcodeAndDeletionIndicator(
			String companyCodeId, String plantId, String warehouseId, String languageId, String partnerItemBarcode, Long deletionIndicator);

	List<ImPartner> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerItemBarcodeAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, String partnerItemBarcode, Long deletionIndicator);

	List<ImPartner> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
			String companyCodeId, String plantId, String warehouseId, String languageId,
			String itemCode, String manufacturerName, Long deletionIndicator);

	List<ImPartner> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPartnerItemBarcodeAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode,
			String manufacturerName, String partnerItemBarcode, Long deletionIndicator);

	List<ImPartner> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndPartnerItemBarcodeAndDeletionIndicator(
			String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode,
			String manufacturerName, String partnerItemBarcode, Long deletionIndicator);
}