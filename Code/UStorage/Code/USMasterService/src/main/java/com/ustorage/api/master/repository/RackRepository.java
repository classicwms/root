package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.rack.Rack;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long>{

	public List<Rack> findAll();

	public Optional<Rack> findByCodeAndDeletionIndicator(String rackId, long l);
}