package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.spanid.SpanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface SpanIdRepository extends JpaRepository<SpanId,Long>, JpaSpecificationExecutor<SpanId> {
	
	public List<SpanId> findAll();
	public Optional<SpanId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndSpanIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId,String aisleId, String spanId,String floorId,String storageSectionId, String languageId, Long deletionIndicator);
}