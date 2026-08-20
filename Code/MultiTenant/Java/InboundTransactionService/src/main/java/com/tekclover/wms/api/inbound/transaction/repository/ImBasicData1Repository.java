package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1;


@Repository
@Transactional
public interface ImBasicData1Repository extends PagingAndSortingRepository<ImBasicData1,Long>,
		JpaSpecificationExecutor<ImBasicData1>, StreamableJpaSpecificationRepository<ImBasicData1> {

	@Query (value = "SELECT TEXT AS description, MFR_PART AS manufacturePart FROM tblimbasicdata1 \r\n"
			+ " WHERE ITM_CODE = :itemCode", nativeQuery = true)
	public List<IImbasicData1> findByItemCode (@Param(value = "itemCode") String itemCode);

	@Query (value = "SELECT TEXT AS description, MFR_PART AS manufacturePart FROM tblimbasicdata1 \r\n"
			+ " WHERE ITM_CODE = :itemCode and C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and IS_DELETED = 0", nativeQuery = true)
	public List<IImbasicData1> findByItemCode (@Param(value = "itemCode") String itemCode,
											   @Param(value = "companyCodeId") String companyCodeId,
											   @Param(value = "plantId") String plantId,
											   @Param(value = "languageId") String languageId);

	public ImBasicData1 findByItemCodeAndWarehouseIdInAndDeletionIndicator(String itemCode, List<String> warehouseId, Long deletionIndicator);
	
	public ImBasicData1 findByItemCodeAndWarehouseIdAndDeletionIndicator(String itemCode, String warehouseId, Long deletionIndicator);

	public ImBasicData1 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, String uom,
			Long deletionIndicator);

	Optional<ImBasicData1> findByItemCodeAndCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
			String itemCode, String companyCodeId, String plantId, String languageId, String warehouseId, Long deletionIndicator);

	ImBasicData1 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId,
			String itemCode, String uom, String manufacturerName, Long deletionIndicator);


	ImBasicData1 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId,
			String itemCode, String manufacturerName, Long deletionIndicator);

	Optional<ImBasicData1> findByItemCodeAndCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndManufacturerPartNoAndDeletionIndicator(
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

	@Query(value = "SELECT SELF_LIFE FROM TBLIMBASICDATA1 \n " +
			"WHERE ITM_CODE = :itemCode and C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID = :warehouseId AND MFR_PART = :manufactureName AND IS_DELETED = 0 ", nativeQuery = true)
	Long getSelfLife(@Param(value = "itemCode") String itemCode,
					   @Param(value = "companyCodeId") String companyCodeId,
					   @Param(value = "plantId") String plantId,
					   @Param(value = "languageId") String languageId,
					   @Param(value = "warehouseId") String warehouseId,
					   @Param(value = "manufactureName") String manufactureName);

    @Query(value = "select * from tblimbasicdata1 where c_id = :companyCodeId " +
            " and LANG_ID = :languageId and plant_id = :plantId  and wh_id = :warehouseId and itm_code = :sourceItemCode " +
            " and ref_field_1 = :targetStorageBin and is_deleted = 0 ",nativeQuery = true)
    ImBasicData1 getItemAndBinV10(@Param(value = "companyCodeId") String companyCodeId,
                                  @Param(value = "languageId") String languageId,
                                  @Param(value = "plantId") String plantId,
                                  @Param(value = "warehouseId") String warehouseId,
                                  @Param(value = "sourceItemCode") String sourceItemCode,
                                  @Param(value = "targetStorageBin") String targetStorageBin);

    @Query(value = "select REF_FIELD_2 from tblimbasicdata1 where c_id = :companyCodeId and plant_id = :plantId and LANG_ID = :languageId " +
            " and wh_id = :warehouseId and itm_code = :itemCode and mfr_part = :manufactureName and  is_deleted = 0 ",nativeQuery = true)
    public String getCustomerIdV9(@Param(value = "companyCodeId") String companyCodeId,
                                  @Param(value = "languageId") String languageId,
                                  @Param(value = "plantId") String plantId,
                                  @Param(value = "warehouseId") String warehouseId,
                                  @Param(value = "itemCode") String itemCode,
                                  @Param(value = "manufactureName") String manufactureName);
}