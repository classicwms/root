package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.rowid.RowId;


@Repository
@Transactional
public interface RowIdRepository extends JpaRepository<RowId,Long>, JpaSpecificationExecutor<RowId> {
	
	public List<RowId> findAll();
	public Optional<RowId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndRowIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long floorId, Long storageSectionId, String rowId, String languageId, Long deletionIndicator);
}