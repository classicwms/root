package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.phase.Phase;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Long>{

	public List<Phase> findAll();

	public Optional<Phase> findByCodeAndDeletionIndicator(String phaseId, long l);
}