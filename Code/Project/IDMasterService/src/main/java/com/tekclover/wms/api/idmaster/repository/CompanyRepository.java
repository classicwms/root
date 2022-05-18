package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.companyid.CompanyId;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyId, Long>{

	public List<CompanyId> findAll();
	Optional<CompanyId> findByCompanyCodeId (String companyId);
}