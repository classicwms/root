package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	@Query(value ="select  tl.bar_typ_id AS barcodeTypeId,tl.bar_typ AS description\n"+
			" from tblbarcodetypeid tl \n" +
			"WHERE \n"+
			"tl.bar_typ_id IN (:barcodeTypeId)and tl.lang_id IN (:languageId)and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getBarcodeTypeIdAndDescription(@Param(value="barcodeTypeId") String barcodeTypeId,
														@Param(value ="languageId")String languageId,
														@Param(value = "companyCodeId")String companyCodeId,
														@Param(value = "plantId")String plantId,
														@Param(value = "warehouseId")String warehouseId);

}