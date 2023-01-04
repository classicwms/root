package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.customergroup.CustomerGroup;

@Repository
public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, Long>{

	public List<CustomerGroup> findAll();

	public Optional<CustomerGroup> findByCodeAndDeletionIndicator(String customerGroupId, long l);
}