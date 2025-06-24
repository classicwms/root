package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.dto.Strategies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
@Transactional
public interface StrategiesRepository extends JpaRepository<Strategies,Long>, JpaSpecificationExecutor<Strategies> {

	public Optional<Strategies> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndSequenceIndicatorAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, Long strategyTypeId, Long sequenceIndicator, Long deletionIndicator);


	@Query(value = "SELECT top 1 partner_nm from tblbusinesspartner WHERE " +
			"(:companyCodeId is null or c_id = :companyCodeId) and " +
			"(:plantId is null or plant_id = :plantId) and " +
			"(:warehouseId is null or wh_id = :warehouseId) and " +
			"(:languageId is null or lang_id = :languageId) and " +
			"(:partnerCode is null or partner_code = :partnerCode) and " +
			"is_deleted = 0", nativeQuery = true)
	String getPartnerName(@Param("companyCodeId") String companyCode,
						  @Param("languageId") String languageId,
						  @Param("plantId") String plantId,
						  @Param("warehouseId") String warehouseId,
						  @Param("partnerCode") String partnerCode);

	@Query(value = "select max(pa_cnf_on) from tblputawayline where barcode_id = :barcodeId and itm_code = :itemCode " +
			" and c_id = :companyId and plant_id = :plantId and wh_id = :warehouseId and is_deleted = 0 \n" +
			"group by barcode_id, itm_code, c_id, plant_id, wh_id ", nativeQuery = true)
	Date getPutawayConfimDate(@Param("barcodeId") String barcodeId,
							  @Param("itemCode") String itemCode,
							  @Param("companyId") String companyId,
							  @Param("plantId") String plantId,
							  @Param("warehouseId") String warehouseId);

}