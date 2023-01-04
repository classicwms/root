package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.requirementtype.RequirementType;

@Repository
public interface RequirementTypeRepository extends JpaRepository<RequirementType, Long>{

	public List<RequirementType> findAll();

	public Optional<RequirementType> findByCodeAndDeletionIndicator(String requirementTypeId, long l);
}