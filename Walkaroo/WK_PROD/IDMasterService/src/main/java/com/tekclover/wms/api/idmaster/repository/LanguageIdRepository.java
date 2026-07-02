package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface LanguageIdRepository extends JpaRepository<LanguageId,Long>, JpaSpecificationExecutor<LanguageId> {
	
	public List<LanguageId> findAll();
	public Optional<LanguageId> 
		findByLanguageIdAndDeletionIndicator(
				String languageId, Long deletionIndicator);
}