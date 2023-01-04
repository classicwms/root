package com.ustorage.api.trans.repository;

import com.ustorage.api.trans.model.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long>,
		JpaSpecificationExecutor<Trip> {

	public List<Trip> findAll();

	public Optional<Trip> findByItemCodeAndDeletionIndicator(String tripId, long l);
}