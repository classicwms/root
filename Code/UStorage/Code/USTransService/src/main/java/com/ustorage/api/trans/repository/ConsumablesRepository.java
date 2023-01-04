package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ustorage.api.trans.model.consumables.Consumables;

@Repository
public interface ConsumablesRepository extends JpaRepository<Consumables, Long>,
		JpaSpecificationExecutor<Consumables>{

	public List<Consumables> findAll();

	public Optional<Consumables> findByItemCodeAndDeletionIndicator(String consumablesId, long l);
}