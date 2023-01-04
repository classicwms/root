package com.ustorage.api.master.repository;

import com.ustorage.api.master.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{

	public List<Employee> findAll();

	public Optional<Employee> findByEmployeeCodeAndDeletionIndicator(String employeeId, long l);
}