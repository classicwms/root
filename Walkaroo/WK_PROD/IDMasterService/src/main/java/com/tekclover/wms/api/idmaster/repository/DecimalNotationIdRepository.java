package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.decimalnotationid.DecimalNotationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface DecimalNotationIdRepository extends JpaRepository<DecimalNotationId,Long>, JpaSpecificationExecutor<DecimalNotationId> {
	
	public List<DecimalNotationId> findAll();
	public Optional<DecimalNotationId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDecimalNotationIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String decimalNotationId, String languageId, Long deletionIndicator);
}