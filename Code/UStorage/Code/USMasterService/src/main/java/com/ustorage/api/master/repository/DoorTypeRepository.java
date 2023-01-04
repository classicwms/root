package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.doortype.DoorType;

@Repository
public interface DoorTypeRepository extends JpaRepository<DoorType, Long>{

	public List<DoorType> findAll();

	public Optional<DoorType> findByCodeAndDeletionIndicator(String doorTypeId, long l);
}