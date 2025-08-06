package com.tekclover.wms.core.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.core.model.IKeyValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.core.model.idmaster.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	public List<User> findAll();
	Optional<User> findByEmail(String email);
	Optional<User> findByUsername(String username);




	@Query(value = "Select c_id as companyCode,lang_id as language,wh_id as warehouse " +
			"from tblwarehouseid where plant_id = :plantId",nativeQuery = true)
	IKeyValuePair getCompanyAndPlant(@Param(value = "plantId") String plantId);
}