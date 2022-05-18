package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.country.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

	List<Country> findAll();
	Optional<Country> findByCountryId (String stateId);
}