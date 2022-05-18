package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.varientid.VariantId;


@Repository
@Transactional
public interface VariantIdRepository extends JpaRepository<VariantId,Long>, JpaSpecificationExecutor<VariantId> {
	
	public List<VariantId> findAll();
	public Optional<VariantId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndVariantCodeAndVariantTypeAndVariantSubCodeAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String variantCode, String variantType, String variantSubCode, Long deletionIndicator);
}