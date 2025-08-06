package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.configuration.Configuration;
import com.tekclover.wms.api.enterprise.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration,Long>, JpaSpecificationExecutor<Configuration>, StreamableJpaSpecificationRepository<Configuration> {

	Optional<Configuration> findByConfigurationIdAndDeletionIndicator(Long configurationId, Long deletionIndicator);

	Optional<Configuration> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, Long deletionIndicator);

	boolean existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, Long deletionIndicator);
}