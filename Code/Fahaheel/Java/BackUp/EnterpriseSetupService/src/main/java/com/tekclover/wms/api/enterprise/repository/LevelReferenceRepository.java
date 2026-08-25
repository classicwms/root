package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.batchserial.LevelReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LevelReferenceRepository extends JpaRepository<LevelReference,String>, JpaSpecificationExecutor<LevelReference> {

    List<LevelReference> findByStorageMethodAndDeletionIndicator(String storageMethod, Long deletionIndicator);
}
