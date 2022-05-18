package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.plant.Plant;

@Repository
@Transactional
public interface PlantRepository extends JpaRepository<Plant,Long>, JpaSpecificationExecutor<Plant> {

	public List<Plant> findAll();
	Optional<Plant> findByPlantId(String plantId);
	
	public Optional<Plant> findByLanguageIdAndCompanyIdAndPlantIdAndDeletionIndicator (
	String languageId, String companyId, String plantId, Long deletionIndicator);
}