package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.PickListLine;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PickListLineRepository extends JpaRepository<PickListLine, Long>,
        JpaSpecificationExecutor<PickListLine>,
        StreamableJpaSpecificationRepository<PickListLine> {

}