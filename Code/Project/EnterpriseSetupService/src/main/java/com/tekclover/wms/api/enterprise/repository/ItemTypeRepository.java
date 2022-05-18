package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.itemtype.ItemType;

@Repository
@Transactional
public interface ItemTypeRepository extends JpaRepository<ItemType,Long>, JpaSpecificationExecutor<ItemType> {

	public List<ItemType> findAll();
	
	public Optional<ItemType> findByItemTypeId(Long itemTypeId);
	
	public Optional<ItemType> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndDeletionIndicator (
	String languageId, String companyId, String plantId, String warehouseId, Long itemTypeId, Long deletionIndicator);
}