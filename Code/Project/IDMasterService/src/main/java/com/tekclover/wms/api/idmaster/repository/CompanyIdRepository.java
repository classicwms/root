package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.companyid.CompanyId;


@Repository
@Transactional
public interface CompanyIdRepository extends JpaRepository<CompanyId,Long>, JpaSpecificationExecutor<CompanyId> {
	
	public List<CompanyId> findAll();
	public Optional<CompanyId> 
		findByCompanyCodeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String languageId, Long deletionIndicator);
}