package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.stocktypeid.StockTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	@Query(value ="select  tl.stck_typ_id AS stockTypeId,tl.stck_typ_text AS description\n"+
			" from tblstocktypeid tl \n" +
			"WHERE \n"+
			"tl.stck_typ_id IN (:stockTypeId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getStockTypeIdAndDescription(@Param(value="stockTypeId") String stockTypeId,
													  @Param(value = "languageId")String languageId,
													  @Param(value = "companyCodeId")String companyCodeId,
													  @Param(value = "plantId")String plantId,
													  @Param(value = "warehouseId")String warehouseId);
}