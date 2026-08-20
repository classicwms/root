package com.tekclover.wms.api.outbound.transaction.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.outbound.transaction.model.outbound.packing.PackingLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface PackingLineRepository extends JpaRepository<PackingLine,Long>, JpaSpecificationExecutor<PackingLine> {

	public List<PackingLine> findAll();

	public Optional<PackingLine>
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPackingNoAndItemCodeAndDeletionIndicator(
				String languageId, Long companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String packingNo, String itemCode, Long deletionIndicator);

	public Optional<PackingLine> findByItemCode(String itemCode);

    @Query(value = "SELECT COUNT(ref_doc_no) AS count FROM (\n"
            + " select distinct ref_doc_no from \n "
            + " tblpickupline WHERE \n"
            + "(:languageId IS NULL OR LANG_ID = :languageId) AND \n"
            + "(:companyCode IS NULL OR C_ID = :companyCode) AND \n"
            + "(:plantId IS NULL OR PLANT_ID = :plantId) AND \n"
            + "(:warehouseId IS NULL OR WH_ID = :warehouseId) AND \n"
            + "(STATUS_ID IN (:statusId)) AND \n"
            + " IS_DELETED = 0 ) x", nativeQuery = true)
    public Long getPackingLineCountV10(@Param("companyCode") List<String> companyCode,
                                       @Param("plantId") List<String> plantId,
                                       @Param("warehouseId") List<String> warehouseId,
                                       @Param("languageId") List<String> languageId,
                                       @Param("statusId") Long statusId);
}