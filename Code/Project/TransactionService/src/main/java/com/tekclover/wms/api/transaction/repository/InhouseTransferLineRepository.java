package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.mnc.InhouseTransferLine;

@Repository
@Transactional
public interface InhouseTransferLineRepository extends JpaRepository<InhouseTransferLine,Long>, JpaSpecificationExecutor<InhouseTransferLine> {

	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize", value="1000"))
	public List<InhouseTransferLine> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param transferNumber
	 * @param sourceItemCode
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<InhouseTransferLine> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndTransferNumberAndSourceItemCodeAndDeletionIndicator(
				String languageId, 
				String companyCodeId, 
				String plantId, 
				String warehouseId, 
				String transferNumber,
				String sourceItemCode,
				Long deletionIndicator);
	
}
