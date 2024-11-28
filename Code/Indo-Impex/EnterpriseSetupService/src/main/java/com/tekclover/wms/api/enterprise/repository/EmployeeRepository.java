package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    public List<Employee> findAll();

    public Optional<Employee> findByEmployeeId(String employeeId);

    public Employee findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIDAndEmployeeIdAndProcessIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String employeeId,
            Long processId, Long l);
}