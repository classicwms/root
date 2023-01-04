package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.workorderstatus.WorkOrderStatus;

@Repository
public interface WorkOrderStatusRepository extends JpaRepository<WorkOrderStatus, Long>{

	public List<WorkOrderStatus> findAll();

	public Optional<WorkOrderStatus> findByCodeAndDeletionIndicator(String workOrderStatusId, long l);
}