package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.shelfid.ShelfId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface ShelfIdRepository extends JpaRepository<ShelfId,Long>, JpaSpecificationExecutor<ShelfId> {
	
	public List<ShelfId> findAll();
	public Optional<ShelfId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndShelfIdAndSpanIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId,String aisleId,String SpanId, String shelfId,String floorId,String storageSectionId, String languageId, Long deletionIndicator);
}