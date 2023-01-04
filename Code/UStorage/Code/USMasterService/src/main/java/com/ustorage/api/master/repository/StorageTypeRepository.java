package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.storagetype.StorageType;

@Repository
public interface StorageTypeRepository extends JpaRepository<StorageType, Long>{

	public List<StorageType> findAll();

	public Optional<StorageType> findByCodeAndDeletionIndicator(String storageTypeId, long l);
}