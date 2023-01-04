package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.workordercreatedby.WorkOrderCreatedBy;

@Repository
public interface WorkOrderCreatedByRepository extends JpaRepository<WorkOrderCreatedBy, Long>{

	public List<WorkOrderCreatedBy> findAll();

	public Optional<WorkOrderCreatedBy> findByCodeAndDeletionIndicator(String workOrderCreatedById, long l);
}