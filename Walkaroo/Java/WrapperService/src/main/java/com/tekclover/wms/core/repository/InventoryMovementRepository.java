package com.tekclover.wms.core.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface InventoryMovementRepository extends JpaRepository<DBInventoryMovement,Long>, JpaSpecificationExecutor<DBInventoryMovement> {
	
	@Async
    CompletableFuture<List<DBInventoryMovement>> readAllBy();
}