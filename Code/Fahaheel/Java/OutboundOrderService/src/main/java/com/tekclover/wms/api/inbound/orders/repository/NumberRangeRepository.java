package com.tekclover.wms.api.inbound.orders.repository;


import com.tekclover.wms.api.inbound.orders.model.numberrange.NumberRange;
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
public interface NumberRangeRepository extends JpaRepository<NumberRange, Long>, JpaSpecificationExecutor<NumberRange> {

	List<NumberRange> findAll();
	Optional<NumberRange> findById(Long id);

	public Optional<NumberRange> findByNumberRangeCodeAndFiscalYearAndWarehouseId(Long numberRangeCode, Long fiscalYear, String warehouseId);

	public Optional<NumberRange> findByNumberRangeCodeAndWarehouseId(Long numberRangeCode, String warehouseId);

	//`LANG_ID`, `C_ID`, `NUM_RAN_CODE`, `NUM_RAN_OBJ`, `IS_DELETED`
	public Optional<NumberRange>
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndNumberRangeCodeAndFiscalYearAndLanguageIdAndDeletionIndicator
		(String companyCodeId, String plantId, String warehouseId, Long numberRangeCode,
				 Long fiscalYear, String languageId, Long deletionIndicator);

	public Optional<NumberRange> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndNumberRangeCodeAndLanguageIdAndDeletionIndicator(
			String companyCodeId, String plantId, String warehouseId, Long numberRangeCode,
			String languageId, Long deletionIndicator);

	@Query(value = "SELECT * from tblnumberrangeid where c_id = :companyCode \n" +
			"AND lang_id = :languageId AND plant_id = :plantId AND wh_id = :warehouseId \n" +
			"AND num_ran_code = :numberRange AND is_deleted = 0", nativeQuery = true)
	Optional<NumberRange> getNextNumberRange(@Param("companyCode") String companyCode,
								   @Param("plantId") String plantId,
								   @Param("warehouseId") String warehouseId,
								   @Param("numberRange") Long numberRange,
								   @Param("languageId") String languageId);
}


