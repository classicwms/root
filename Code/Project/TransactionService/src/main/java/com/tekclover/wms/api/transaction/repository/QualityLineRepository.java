package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;

@Repository
@Transactional
public interface QualityLineRepository extends JpaRepository<QualityLine,Long>, JpaSpecificationExecutor<QualityLine> {
	
	public List<QualityLine> findAll();
	
	public Optional<QualityLine> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
				String languageId, Long companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, Long deletionIndicator);
	
	public Optional<QualityLine> findByPartnerCode(String partnerCode);
	
	public QualityLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, long l);

	public QualityLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String qualityInspectionNo, String itemCode, long l);
}