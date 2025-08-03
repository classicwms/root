package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.statusmessagesid.StatusMessagesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface StatusMessagesIdRepository extends JpaRepository<StatusMessagesId,Long>, JpaSpecificationExecutor<StatusMessagesId> {
	
	public List<StatusMessagesId> findAll();
	public Optional<StatusMessagesId> 
		findByMessageIdAndLanguageIdAndMessageTypeAndDeletionIndicator(
				String messagesId, String languageId, String messageType, Long deletionIndicator);
}