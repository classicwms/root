package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.itemtypeid.ItemTypeId;


@Repository
@Transactional
public interface ItemTypeIdRepository extends JpaRepository<ItemTypeId,Long>, JpaSpecificationExecutor<ItemTypeId> {
	
	public List<ItemTypeId> findAll();
	public Optional<ItemTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemTypeAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long itemTypeId, String itemType, Long deletionIndicator);
}