package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.batchserial.BatchSerial;

@Repository
@Transactional
public interface BatchSerialRepository extends JpaRepository<BatchSerial,Long>, JpaSpecificationExecutor<BatchSerial> {

	public List<BatchSerial> findAll();
	Optional<BatchSerial> findByStorageMethod(String storageMethod);
}