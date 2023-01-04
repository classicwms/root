package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.customertype.CustomerType;

@Repository
public interface CustomerTypeRepository extends JpaRepository<CustomerType, Long>{

	public List<CustomerType> findAll();

	public Optional<CustomerType> findByCodeAndDeletionIndicator(String customerTypeId, long l);
}