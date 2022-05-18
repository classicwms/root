package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;

@Repository
@Transactional
public interface OutboundHeaderRepository extends JpaRepository<OutboundHeader,Long>, JpaSpecificationExecutor<OutboundHeader> {
	
	public List<OutboundHeader> findAll();
	
	public Optional<OutboundHeader> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, 
				String refDocNumber, String partnerCode, Long deletionIndicator);
	
	public Optional<OutboundHeader> findByPreOutboundNo(String preOutboundNo);
	
	public OutboundHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long deletionIndicator);

	public OutboundHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String referenceField2, Long deletionIndicator);

	public OutboundHeader findByRefDocNumberAndDeletionIndicator(String refDocNumber, long l);

}