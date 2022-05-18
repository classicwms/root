package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;

@Repository
@Transactional
public interface InboundHeaderRepository extends JpaRepository<InboundHeader,Long>, JpaSpecificationExecutor<InboundHeader> {
	
	public List<InboundHeader> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param refDocNumber
	 * @param preInboundNo
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<InboundHeader> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
				String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, 
				String preInboundNo, Long deletionIndicator);
	
	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param refDocNumber
	 * @param preInboundNo
	 * @param l
	 * @return
	 */
	public List<InboundHeader> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String refDocNumber, String preInboundNo, Long deletionIndicator);

	public List<InboundHeader> findByWarehouseIdAndStatusIdAndDeletionIndicator(String warehouseId, long l, long m);

	public List<InboundHeader> findByWarehouseIdAndDeletionIndicator(String warehouseId, long l);
}