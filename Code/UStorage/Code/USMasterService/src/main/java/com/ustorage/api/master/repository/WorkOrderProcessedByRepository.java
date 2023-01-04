package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.workorderprocessedby.WorkOrderProcessedBy;

@Repository
public interface WorkOrderProcessedByRepository extends JpaRepository<WorkOrderProcessedBy, Long>{

	public List<WorkOrderProcessedBy> findAll();

	public Optional<WorkOrderProcessedBy> findByCodeAndDeletionIndicator(String workOrderProcessedById, long l);
}