package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.subitemgroupid.SubItemGroupId;


@Repository
@Transactional
public interface SubItemGroupIdRepository extends JpaRepository<SubItemGroupId,Long>, JpaSpecificationExecutor<SubItemGroupId> {
	
	public List<SubItemGroupId> findAll();
	public Optional<SubItemGroupId>
	findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndSubItemGroupIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long itemTypeId, Long itemGroupId,
				Long subItemGroupId, String languageId, Long deletionIndicator);
}