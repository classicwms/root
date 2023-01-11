package com.ustorage.api.master.repository;

import com.ustorage.api.master.model.documentstorage.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentStorageRepository extends JpaRepository<DocumentStorage, Long>,
		JpaSpecificationExecutor<DocumentStorage> {

	public List<DocumentStorage> findAll();

	public Optional<DocumentStorage> findByDocumentNumberAndDeletionIndicator(String documentNumber, long l);

}