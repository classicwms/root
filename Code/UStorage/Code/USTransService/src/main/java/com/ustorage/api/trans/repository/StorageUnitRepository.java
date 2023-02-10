package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import com.ustorage.api.trans.model.leadcustomer.LeadCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ustorage.api.trans.model.storageunit.StorageUnit;

@Repository
public interface StorageUnitRepository extends JpaRepository<StorageUnit, Long>,
		JpaSpecificationExecutor<StorageUnit> {

	public List<StorageUnit> findAll();

	public Optional<StorageUnit> findByItemCodeAndDeletionIndicator(String itemCode, long l);

	public Optional<StorageUnit> findByCodeIdAndDeletionIndicator(String codeId, long l);
}