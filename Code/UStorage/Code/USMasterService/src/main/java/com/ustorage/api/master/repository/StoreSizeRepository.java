package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.storesize.StoreSize;

@Repository
public interface StoreSizeRepository extends JpaRepository<StoreSize, Long>{

	public List<StoreSize> findAll();

	public Optional<StoreSize> findByCodeAndDeletionIndicator(String storeSizeId, long l);
}