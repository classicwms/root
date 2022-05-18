package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundHeader;

@Repository
@Transactional
public interface PreOutboundHeaderRepository extends JpaRepository<PreOutboundHeader,Long>, JpaSpecificationExecutor<PreOutboundHeader> {
	
	public List<PreOutboundHeader> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param refDocNumber
	 * @param preOutboundNo
	 * @param partnerCode
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<PreOutboundHeader> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, 
				String preOutboundNo, String partnerCode, Long deletionIndicator);

	public PreOutboundHeader findByPreOutboundNo(String preOutboundNo);
}