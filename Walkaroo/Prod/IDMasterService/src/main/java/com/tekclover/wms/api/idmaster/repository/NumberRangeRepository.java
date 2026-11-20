package com.tekclover.wms.api.idmaster.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.numberrange.NumberRange;

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
}


