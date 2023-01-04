package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.rentperiod.RentPeriod;

@Repository
public interface RentPeriodRepository extends JpaRepository<RentPeriod, Long>{

	public List<RentPeriod> findAll();

	public Optional<RentPeriod> findByCodeAndDeletionIndicator(String rentPeriodId, long l);
}