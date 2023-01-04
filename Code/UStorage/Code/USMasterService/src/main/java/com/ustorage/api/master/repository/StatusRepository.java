package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.status.Status;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long>{

	public List<Status> findAll();

	public Optional<Status> findByCodeAndDeletionIndicator(String statusId, long l);
}