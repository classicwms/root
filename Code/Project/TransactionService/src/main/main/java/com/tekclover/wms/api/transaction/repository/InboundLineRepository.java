package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.InboundLine;

@Repository
@Transactional
public interface InboundLineRepository extends JpaRepository<InboundLine,Long>, JpaSpecificationExecutor<InboundLine> {
	
	/**
	 * 
	 */
	public List<InboundLine> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param refDocNumber
	 * @param preInboundNo
	 * @param lineNo
	 * @param itemCode
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<InboundLine> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
				String languageId, 
				String companyCode, 
				String plantId, 
				String warehouseId, 
				String refDocNumber, 
				String preInboundNo, 
				Long lineNo, 
				String itemCode, 
				Long deletionIndicator);

	public List<InboundLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
			String preInboundNo, Long deletionIndicator);

	
	// For Sending confirmation API
	public List<InboundLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndReferenceField1AndStatusIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
			String preInboundNo, String referenceField1, Long statusId, Long deletionIndicator);

	public List<InboundLine> findByRefDocNumberAndDeletionIndicator(String refDocNumber, long l);
}

