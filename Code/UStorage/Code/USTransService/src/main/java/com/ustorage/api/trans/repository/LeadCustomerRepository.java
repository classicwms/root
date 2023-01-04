package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ustorage.api.trans.model.leadcustomer.LeadCustomer;

@Repository
public interface LeadCustomerRepository extends JpaRepository<LeadCustomer, Long>,
		JpaSpecificationExecutor<LeadCustomer> {

	public List<LeadCustomer> findAll();

	public Optional<LeadCustomer> findByCustomerCode(String leadCustomerId);
}