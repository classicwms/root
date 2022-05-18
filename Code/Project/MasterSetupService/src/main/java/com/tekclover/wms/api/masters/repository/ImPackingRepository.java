package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.impacking.ImPacking;

@Repository
@Transactional
public interface ImPackingRepository extends JpaRepository<ImPacking,Long>, JpaSpecificationExecutor<ImPacking> {

	Optional<ImPacking> findByPackingMaterialNo(String packingMaterialNo);
}