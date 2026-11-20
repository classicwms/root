package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.dockid.DockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface DockIdRepository extends JpaRepository<DockId,Long>, JpaSpecificationExecutor<DockId> {
	
	public List<DockId> findAll();
	public Optional<DockId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDockIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String dockId, String languageId, Long deletionIndicator);
}