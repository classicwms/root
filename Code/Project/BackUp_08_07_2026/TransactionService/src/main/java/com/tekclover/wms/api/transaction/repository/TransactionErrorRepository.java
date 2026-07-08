package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.dto.TransactionError;

@Repository
@Transactional
public interface TransactionErrorRepository extends JpaRepository<TransactionError, Long>,
        StreamableJpaSpecificationRepository<TransactionError> {
	
}