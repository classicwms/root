package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;

@Repository
@Transactional
public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner,Long>, JpaSpecificationExecutor<BusinessPartner> {

	Optional<BusinessPartner> findByPartnerCode(String partnerCode);
}