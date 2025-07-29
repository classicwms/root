package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.country.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long>, JpaSpecificationExecutor<Country> {

	List<Country> findAll();
	Optional<Country> findByCountryIdAndLanguageId (String countryId,String languageId);

	@Query(value ="select  tl.country_id AS countryId,tl.country_nm AS description\n"+
			" from tblcountryid tl \n" +
			"WHERE \n"+
			"tl.country_id IN (:countryId)and tl.lang_id IN (:languageId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getCountryIdAndDescription(@Param(value="countryId") String countryId,
													@Param(value = "languageId")String languageId);
}