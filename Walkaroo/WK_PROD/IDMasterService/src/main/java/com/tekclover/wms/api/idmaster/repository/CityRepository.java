package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.city.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long>, JpaSpecificationExecutor<City> {

	public List<City> findAll();
	public Optional<City> findByCityIdAndStateIdAndCountryIdAndLanguageId(String cityId, String stateId, String countryId, String languageId);
}