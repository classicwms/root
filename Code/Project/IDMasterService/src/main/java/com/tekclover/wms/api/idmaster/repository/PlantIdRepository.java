package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.plantid.PlantId;


@Repository
@Transactional
public interface PlantIdRepository extends JpaRepository<PlantId,Long>, JpaSpecificationExecutor<PlantId> {
	
	public List<PlantId> findAll();
	public Optional<PlantId> 
		findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String languageId, Long deletionIndicator);
}