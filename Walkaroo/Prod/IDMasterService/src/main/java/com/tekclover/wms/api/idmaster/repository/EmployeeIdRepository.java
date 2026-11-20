package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.employeeid.EmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface EmployeeIdRepository extends JpaRepository<EmployeeId,Long>, JpaSpecificationExecutor<EmployeeId> {
	
	public List<EmployeeId> findAll();
	public Optional<EmployeeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndEmployeeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String employeeId, String languageId, Long deletionIndicator);
}