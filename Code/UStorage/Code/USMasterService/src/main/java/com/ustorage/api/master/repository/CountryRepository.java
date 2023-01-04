package com.ustorage.api.master.repository;

import com.ustorage.api.master.model.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long>{

	public List<Country> findAll();

	public Optional<Country> findByCodeAndDeletionIndicator(String countryId, long l);
}