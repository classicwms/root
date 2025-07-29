package com.tekclover.wms.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.core.batch.scheduler.entity.StorageBin;

@Repository
@Transactional
public interface StorageBinRepository extends JpaRepository<StorageBin,Long>, JpaSpecificationExecutor<StorageBin> {

	@Query (value = "SELECT ST_SEC_ID FROM tblstoragebin \r\n"
			+ " WHERE ST_BIN = :storageBin", nativeQuery = true)
	public String findByStorageBin (@Param(value = "storageBin") String storageBin);


	Optional<StorageBin> findByStorageBinAndDeletionIndicator(String storageBin, Long delFlag);
}


