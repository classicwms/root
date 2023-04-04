package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.itemgroupid.ItemGroupId;


@Repository
@Transactional
public interface ItemGroupIdRepository extends JpaRepository<ItemGroupId,Long>, JpaSpecificationExecutor<ItemGroupId> {
	
	public List<ItemGroupId> findAll();
	public Optional<ItemGroupId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long itemTypeId, Long itemGroupId, Long deletionIndicator);
}