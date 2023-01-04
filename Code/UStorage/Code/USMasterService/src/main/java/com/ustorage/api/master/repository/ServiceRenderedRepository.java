package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.servicerendered.ServiceRendered;

@Repository
public interface ServiceRenderedRepository extends JpaRepository<ServiceRendered, Long>{

	public List<ServiceRendered> findAll();

	public Optional<ServiceRendered> findByCodeAndDeletionIndicator(String serviceRenderedId, long l);
}