package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.refdoctypeid.RefDocTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface RefDocTypeIdRepository extends JpaRepository<RefDocTypeId,Long>, JpaSpecificationExecutor<RefDocTypeId> {
	
	public List<RefDocTypeId> findAll();
	public Optional<RefDocTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String referenceDocumentTypeId, String languageId, Long deletionIndicator);
}