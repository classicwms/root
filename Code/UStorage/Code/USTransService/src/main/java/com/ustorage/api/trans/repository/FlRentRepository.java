package com.ustorage.api.trans.repository;

import com.ustorage.api.trans.model.flrent.FlRent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlRentRepository extends JpaRepository<FlRent, Long>,
		JpaSpecificationExecutor<FlRent> {

	public List<FlRent> findAll();

	public Optional<FlRent> findByItemCodeAndDeletionIndicator(String flRentId, long l);
}