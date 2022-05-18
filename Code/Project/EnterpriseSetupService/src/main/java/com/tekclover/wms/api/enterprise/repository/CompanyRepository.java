package com.tekclover.wms.api.enterprise.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.company.Company;

@Repository
@Transactional
public interface CompanyRepository extends JpaRepository<Company,Long>, JpaSpecificationExecutor<Company> {

	public Optional<Company> findByCompanyId(String companyId);
	
	public Optional<Company> findByLanguageIdAndCompanyIdAndDeletionIndicator (
			String languageId, String companyId, Long deletionIndicator);
}