package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.outbound.v2.PickListHeader;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PickListHeaderRepository extends JpaRepository<PickListHeader, Long>,
        JpaSpecificationExecutor<PickListHeader>,
        StreamableJpaSpecificationRepository<PickListHeader> {


}