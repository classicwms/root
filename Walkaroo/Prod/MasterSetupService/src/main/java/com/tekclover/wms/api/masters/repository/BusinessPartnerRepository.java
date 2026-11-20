package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.businesspartner.v2.BusinessPartnerV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;

@Repository
@Transactional
public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner,Long>, JpaSpecificationExecutor<BusinessPartner> {

	Optional<BusinessPartner> findByPartnerCode(String partnerCode);
	// BusinessPartner findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndLanguageIdAndDeletionIndicator(String companyCodeId,String plantId,String warehouseId,String partnerCode,String languageId,Long deletionIndicator);

	Optional<BusinessPartner> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndBusinessPartnerTypeAndLanguageIdAndDeletionIndicator(String companyCodeId,String plantId,String warehouseId,String partnerCode,Long businessPartnerType,
																																			   String languageId,Long deletionIndicator);

	@Query(value ="select  tl.partner_code AS partnerCode,tl.partner_nm AS description \n"+
			" from tblbusinesspartner tl \n" +
			"WHERE \n"+
			"tl.partner_code IN (:partnerCode) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and tl.partner_typ IN (:partnerType) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getPartnerCodeAndDescription(@Param(value="partnerCode") String partnerCode,
												   @Param(value = "languageId") String languageId,
												   @Param(value = "companyCodeId")String companyCodeId,
												   @Param(value = "plantId")String plantId,
												   @Param(value = "warehouseId")String warehouseId,
													  @Param(value = "partnerType")Long partnerType);


    Optional<BusinessPartnerV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
			String companyCodeId, String plantId, String en, String warehouseId, String partnerCode, long l);
}