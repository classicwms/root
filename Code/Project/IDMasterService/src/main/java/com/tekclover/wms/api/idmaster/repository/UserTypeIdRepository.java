package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.usertypeid.UserTypeId;

@Repository
@Transactional
public interface UserTypeIdRepository extends JpaRepository<UserTypeId,Long>, JpaSpecificationExecutor<UserTypeId> {
	
	public List<UserTypeId> findAll();
	public Optional<UserTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndUserTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long userTypeId, String languageId, Long deletionIndicator);
}