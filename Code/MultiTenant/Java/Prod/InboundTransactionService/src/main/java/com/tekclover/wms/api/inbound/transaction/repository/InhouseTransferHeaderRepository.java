package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.mnc.InhouseTransferHeader;

@Repository
@Transactional
public interface InhouseTransferHeaderRepository extends JpaRepository<InhouseTransferHeader,Long>,
		JpaSpecificationExecutor<InhouseTransferHeader>, StreamableJpaSpecificationRepository<InhouseTransferHeader> {

	public List<InhouseTransferHeader> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param transferNumber
	 * @param transferTypeId
	 * @return
	 */
	public Optional<InhouseTransferHeader> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndTransferNumberAndTransferTypeIdAndDeletionIndicator(
				String languageId, 
				String companyCodeId, 
				String plantId, 
				String warehouseId, 
				String transferNumber,
				Long transferTypeId, Long deletionIndicator);
	
}
