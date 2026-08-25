package com.tekclover.wms.api.masters.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.impl.ItemListImpl;
import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;

@Repository
@Transactional
public interface ImBasicData1Repository extends PagingAndSortingRepository<ImBasicData1,Long>, 
												JpaSpecificationExecutor<ImBasicData1>,
												StreamableJpaSpecificationRepository<ImBasicData1> {

	public Optional<ImBasicData1> findByItemCodeAndWarehouseIdAndDeletionIndicator(String itemCode, String warehouseId, Long deletionIndicator);
	public List<ImBasicData1> findByItemCodeLikeAndDescriptionLike(String itemCode, String itemDesc);

   public Optional<ImBasicData1>  findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
		   String companyCodeId,String plantId,String warehouseId, String itemCode,String uomId,String manufacturerPartNo,String languageId,Long deletionIndicator);

	@Query(value ="select  tl.itm_code AS itemCode,tl.text AS description\n"+
			" from tblimbasicdata1 tl \n" +
			"WHERE \n"+
			"tl.wh_id IN (:warehouseId)and tl.lang_id IN (:languageId) and tl.itm_code IN (:itemCode) and tl.c_id IN (:companyCodeId) and tl.uom_id IN (:uomId) and tl.plant_id IN (:plantId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getItemCodeAndDescription(@Param(value="warehouseId") String warehouseId,
													  @Param(value="languageId")String languageId,
													  @Param(value = "itemCode")String itemCode,
													  @Param(value = "companyCodeId")String companyCodeId,
													  @Param(value = "plantId")String plantId,
													  @Param(value = "uomId")String uomId);


   @Query(
			value = "select TOP 50 itm_code as itemCode ,text as description from tblimbasicdata1 \n" +
					"where ( itm_code like :searchText1% or itm_code like %:searchText2 \n" +
					"or text like %:searchText3% ) \n" +
					"group by itm_code,text ",
			nativeQuery = true
	)
	List<ItemListImpl> getItemListBySearch(@Param("searchText1") String searchText1,
										   @Param("searchText2") String searchText2,
										   @Param("searchText3") String searchText3);

   @Query(
			value = "select TOP 50 itm_code as itemCode, mfr_part manufacturerName, \n" +
					"text as description from tblimbasicdata1 \n" +
					"where ( itm_code like :searchText1% or itm_code like %:searchText2 \n" +
					"or text like %:searchText3% ) and \n" +
					"c_id in (:companyCodeId) and plant_id in (:plantId) and lang_id in (:languageId) and wh_id in (:warehouseId)\n" +
					"group by itm_code,mfr_part,text ",
			nativeQuery = true
	)
	List<ItemListImpl> getItemListBySearchNew(	@Param("searchText1") String searchText1,
												@Param("searchText2") String searchText2,
												@Param("searchText3") String searchText3,
												@Param("companyCodeId") String companyCodeId,
												@Param("plantId") String plantId,
												@Param("languageId") String languageId,
												@Param("warehouseId") String warehouseId  );

   @Query(value = 	"select TOP 50 itm_code as itemCode, mfr_part manufacturerName, \n" +
					"text as description from tblimbasicdata1 \n" +
					"where ( itm_code like :searchText1% or itm_code like %:searchText2 \n" +
					"or text like %:searchText3% ) and \n" +
		   			"(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
		   			"(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
		   			"(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
		   			"(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) \n" +
					"group by itm_code,mfr_part,text ", nativeQuery = true )
	List<ItemListImpl> getItemListBySearchV2(	@Param("searchText1") String searchText1,
												@Param("searchText2") String searchText2,
												@Param("searchText3") String searchText3,
												@Param("companyCodeId") String companyCodeId,
												@Param("plantId") String plantId,
												@Param("languageId") String languageId,
												@Param("warehouseId") String warehouseId  );

    Optional<ImBasicData1> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
			String companyCodeId, String plantId, String warehouseId, String itemCode, String manufacturerPartNo, String languageId, Long deletionIndicator);
}