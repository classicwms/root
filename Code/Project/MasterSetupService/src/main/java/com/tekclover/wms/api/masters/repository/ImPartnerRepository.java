package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.impartner.ImPartner;

@Repository
@Transactional
public interface ImPartnerRepository extends JpaRepository<ImPartner,Long>, JpaSpecificationExecutor<ImPartner> {

	Optional<ImPartner> findByBusinessPartnerCode(String businessPartnerCode);
}