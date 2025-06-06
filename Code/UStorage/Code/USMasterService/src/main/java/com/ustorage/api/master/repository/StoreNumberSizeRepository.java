package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.storenumbersize.StoreNumberSize;

@Repository
public interface StoreNumberSizeRepository extends JpaRepository<StoreNumberSize, Long>{

	public List<StoreNumberSize> findAll();

	public Optional<StoreNumberSize> findByCodeAndDeletionIndicator(String storeNumberSizeId, long l);
}