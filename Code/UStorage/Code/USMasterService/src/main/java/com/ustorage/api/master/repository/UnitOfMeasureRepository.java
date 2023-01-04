package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.unitofmeasure.UnitOfMeasure;

@Repository
public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, Long>{

	public List<UnitOfMeasure> findAll();

	public Optional<UnitOfMeasure> findByCodeAndDeletionIndicator(String unitOfMeasureId, long l);
}