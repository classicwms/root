package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.packingmaterial.PackingMaterial;

@Repository
@Transactional
public interface PackingMaterialRepository extends JpaRepository<PackingMaterial,Long>, JpaSpecificationExecutor<PackingMaterial> {

	Optional<PackingMaterial> findByPackingMaterialNo(String packingMaterialNo);
}