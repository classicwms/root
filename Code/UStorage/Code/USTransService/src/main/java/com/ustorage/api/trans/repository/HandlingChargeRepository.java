package com.ustorage.api.trans.repository;

import com.ustorage.api.trans.model.handlingcharge.HandlingCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HandlingChargeRepository extends JpaRepository<HandlingCharge, Long>,
		JpaSpecificationExecutor<HandlingCharge> {

	public List<HandlingCharge> findAll();

	public Optional<HandlingCharge> findByItemCodeAndDeletionIndicator(String handlingChargeId, long l);
}