package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.itemgroup.ItemGroup;

@Repository
@Transactional
public interface ItemGroupRepository extends JpaRepository<ItemGroup,Long>, JpaSpecificationExecutor<ItemGroup> {
	public List<ItemGroup> findAll();
	
	public Optional<ItemGroup> findByItemGroupId(Long itemGroupId);
	
	public Optional<ItemGroup> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndSubItemGroupIdAndDeletionIndicator (
	String languageId, String companyId, String plantId, String warehouseId, Long itemTypeId, 
	Long itemGroupId, Long subItemGroupId, Long deletionIndicator);
}

