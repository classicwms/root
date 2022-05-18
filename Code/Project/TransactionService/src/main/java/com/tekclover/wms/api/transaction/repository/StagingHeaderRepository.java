package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;

@Repository
@Transactional
public interface StagingHeaderRepository extends JpaRepository<StagingHeader,Long>, JpaSpecificationExecutor<StagingHeader> {
	/**
	 * 
	 */
	public List<StagingHeader> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param stagingNo
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<StagingHeader> 
		findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndDeletionIndicator(
				String languageId, 
				String companyCode, 
				String plantId, 
				String warehouseId, 
				String preInboundNo, 
				String refDocNumber, 
				String stagingNo, 
				Long deletionIndicator);

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
	public List<StagingHeader> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
			String preInboundNo, Long deletionIndicator);
	
	/**
	 * 
	 * @param warehouseId
	 * @param statusId
	 * @param deletionIndicator
	 * @return
	 */
	public List<StagingHeader> findByWarehouseIdAndStatusIdAndDeletionIndicator (String warehouseId, 
			Long statusId, Long deletionIndicator);
}