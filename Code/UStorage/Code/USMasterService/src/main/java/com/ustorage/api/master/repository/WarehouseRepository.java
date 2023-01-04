package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.warehouse.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>{

	public List<Warehouse> findAll();

	public Optional<Warehouse> findByCodeAndDeletionIndicator(String warehouseId, long l);
}