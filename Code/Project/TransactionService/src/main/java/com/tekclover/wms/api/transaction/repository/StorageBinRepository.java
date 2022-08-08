package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.dto.StorageBin;

@Repository
@Transactional
public interface StorageBinRepository extends JpaRepository<StorageBin,Long>, JpaSpecificationExecutor<StorageBin> {

	public List<StorageBin> findByStorageBinInAndStorageSectionIdInAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
			List<String> storageBin, List<String> storageSectionId, Integer putawayBlock, Integer pickingBlock, Long deletionIndicator);

	@Query (value = "SELECT ST_SEC_ID FROM tblstoragebin \r\n"
			+ " WHERE ST_BIN = :storageBin", nativeQuery = true)
	public String findByStorageBin (@Param(value = "storageBin") String storageBin);


	Optional<StorageBin> getByStorageBinForInhouseUpdate(String storageBin);
	
}


