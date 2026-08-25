package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import com.tekclover.wms.api.inbound.orders.model.dto.IImbasicData1;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ImBasicData1V2Repository extends PagingAndSortingRepository<ImBasicData1V2,Long>,
		JpaSpecificationExecutor<ImBasicData1V2>, StreamableJpaSpecificationRepository<ImBasicData1V2> {

	@Query (value = "SELECT TEXT AS description, MFR_PART AS manufacturePart FROM tblimbasicdata1 \r\n"
			+ " WHERE ITM_CODE = :itemCode", nativeQuery = true)
	public List<IImbasicData1> findByItemCode (@Param(value = "itemCode") String itemCode);

	@Query (value = "SELECT TEXT AS description, MFR_PART AS manufacturePart FROM tblimbasicdata1 \r\n"
			+ " WHERE ITM_CODE = :itemCode and C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and IS_DELETED = 0", nativeQuery = true)
	public List<IImbasicData1> findByItemCode (@Param(value = "itemCode") String itemCode,
											   @Param(value = "companyCodeId") String companyCodeId,
											   @Param(value = "plantId") String plantId,
											   @Param(value = "languageId") String languageId);

	public ImBasicData1V2 findByItemCodeAndWarehouseIdInAndDeletionIndicator(String itemCode, List<String> warehouseId, Long deletionIndicator);
	
	public ImBasicData1V2 findByItemCodeAndWarehouseIdAndDeletionIndicator(String itemCode, String warehouseId, Long deletionIndicator);

	public ImBasicData1V2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, String uom,
			Long deletionIndicator);

	Optional<ImBasicData1V2> findByItemCodeAndCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
			String itemCode, String companyCodeId, String plantId, String languageId, String warehouseId, Long deletionIndicator);

	ImBasicData1V2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId,
			String itemCode, String uom, String manufacturerName, Long deletionIndicator);


	ImBasicData1V2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId,
			String itemCode, String manufacturerName, Long deletionIndicator);

	ImBasicData1V2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId,
			String itemCode, Long deletionIndicator);


	ImBasicData1V2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId,
			String itemCode, String manufacturerName, Long deletionIndicator);
//	@Query(value = "SELECT * FROM tblimbasicdata1 WHERE LANG_ID = ?1 AND C_ID = ?2 AND PLANT_ID = ?3 AND WH_ID = ?4 AND ITM_CODE = ?5 AND MFR_PART = ?6 AND IS_DELETED = ?7",
//			nativeQuery = true)
//	ImBasicData1V2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(String languageId, String companyCode, String plantId,
//								String warehouseId, String itemCode,
//								String manufacturerName, Long deletionIndicator);

	Optional<ImBasicData1V2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
			String companyCodeId, String plantId, String warehouseId, String itemCode, String uomId, String manufacturerPartNo, String languageId, long l);

	Optional<ImBasicData1V2> findByItemCodeAndCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndManufacturerPartNoAndDeletionIndicator(
			String itemCode, String companyCodeId, String plantId, String languageId,
			String warehouseId, String manufacturerName, Long deletionIndicator);

	@Query (value = "SELECT cap_chk capacityCheck, ITM_CODE description FROM tblimbasicdata1 \r\n"
			+ " WHERE ITM_CODE = :itemCode and C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID = :warehouseId AND MFR_PART = :manufactureName AND IS_DELETED = 0", nativeQuery = true)
	public IImbasicData1 findCapacityCheck (@Param(value = "itemCode") String itemCode,
											   @Param(value = "companyCodeId") String companyCodeId,
											   @Param(value = "plantId") String plantId,
											   @Param(value = "languageId") String languageId,
											   @Param(value = "warehouseId") String warehouseId,
											   @Param(value = "manufactureName") String manufactureName);


	@Query(value = "select tc.c_text companyDesc,\n" +
			"tp.plant_text plantDesc,\n" +
			"tw.wh_text warehouseDesc from \n" +
			"tblcompanyid tc\n" +
			"join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id\n" +
			"join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id=tc.lang_id and tw.plant_id = tp.plant_id\n" +
			"where\n" +
			"tc.c_id IN (:companyCodeId) and \n" +
			"tc.lang_id IN (:languageId) and \n" +
			"tp.plant_id IN(:plantId) and \n" +
			"tw.wh_id IN (:warehouseId) and \n" +
			"tc.is_deleted=0 and \n" +
			"tp.is_deleted=0 and \n" +
			"tw.is_deleted=0", nativeQuery = true)
	public IKeyValuePair getDescription(@Param(value = "companyCodeId") String companyCodeId,
										@Param(value = "languageId") String languageId,
										@Param(value = "plantId") String plantId,
										@Param(value = "warehouseId") String warehouseId);
}
