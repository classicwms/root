package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.crossdock.CrossDockUnallocated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CrossDockUnallocatedRepository extends JpaRepository<CrossDockUnallocated, String>, JpaSpecificationExecutor<CrossDockUnallocated> {
}
