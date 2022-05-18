package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.imalternateuom.ImAlternateUom;

@Repository
@Transactional
public interface ImAlternateUomRepository extends JpaRepository<ImAlternateUom,Long>, JpaSpecificationExecutor<ImAlternateUom> {

	Optional<ImAlternateUom> findByAlternateUom(String alternateUom);
}