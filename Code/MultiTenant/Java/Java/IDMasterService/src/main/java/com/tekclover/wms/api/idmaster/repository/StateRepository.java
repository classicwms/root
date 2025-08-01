package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.state.State;

@Repository
public interface StateRepository extends JpaRepository<State, Long>, JpaSpecificationExecutor<State> {

	List<State> findAll();
	Optional<State> findByStateIdAndCountryIdAndLanguageId (String stateId,String countryId,String languageId);

	@Query(value ="select  tl.state_id AS stateId,tl.state_nm AS description\n"+
			" from tblstateid tl \n" +
			"WHERE \n"+
			"tl.state_id IN (:stateId)and tl.lang_id IN (:languageId) and tl.country_id IN (:countryId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getStateIdAndDescription(@Param(value="stateId") String stateId,
												  @Param(value = "languageId")String languageId,
												  @Param(value = "countryId")String countryId);
}