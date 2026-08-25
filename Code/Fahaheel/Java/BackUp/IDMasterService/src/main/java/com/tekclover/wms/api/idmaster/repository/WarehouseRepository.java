package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;


@Repository
@Transactional
public interface WarehouseRepository extends JpaRepository<Warehouse,Long>, JpaSpecificationExecutor<Warehouse> {
	
	public List<Warehouse> findAll();
	
	/**
	 * 
	 * @param companyCodeId
	 * @param warehouseId
	 * @param languageId
	 * @param plantId
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<Warehouse> 
		findByCompanyCodeIdAndWarehouseIdAndLanguageIdAndPlantIdAndDeletionIndicator(
				String companyCodeId, String warehouseId, String languageId, String plantId, Long deletionIndicator);

	@Query(value ="select  tl.wh_id AS warehouseId,tl.wh_text AS description\n"+
			" from tblwarehouseid tl \n" +
			"WHERE \n"+
			"tl.wh_id IN (:warehouseId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN(:plantId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)
	public IKeyValuePair getWarehouseIdAndDescription(@Param(value="warehouseId") String warehouseId,
													  @Param(value="languageId")String languageId,
													  @Param(value = "companyCodeId")String companyCodeId,
													  @Param(value = "plantId")String plantId);

	public Optional<Warehouse> findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(String companyCodeId,
			String plantId, String languageId, long l);

	Warehouse findByWarehouseIdAndDeletionIndicator(
			String warehouseId, Long deletionIndicator
	);

	Warehouse findTop1ByWarehouseIdAndDeletionIndicator(
			String warehouseId, Long deletionIndicator
	);

}