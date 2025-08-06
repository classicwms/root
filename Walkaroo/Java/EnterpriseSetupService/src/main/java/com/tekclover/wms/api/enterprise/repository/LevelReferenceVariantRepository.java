package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.variant.LevelReferenceVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LevelReferenceVariantRepository extends JpaRepository<LevelReferenceVariant,Long>, JpaSpecificationExecutor<LevelReferenceVariant> {
    List<LevelReferenceVariant> findByVariantIdAndDeletionIndicator(String storageMethod, Long deletionIndicator);
}
