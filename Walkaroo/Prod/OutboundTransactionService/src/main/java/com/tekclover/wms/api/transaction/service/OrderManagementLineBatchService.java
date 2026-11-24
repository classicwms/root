package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.repository.OrderManagementLineV2Repository;
import com.tekclover.wms.api.transaction.repository.PickupHeaderV2Repository;
import com.tekclover.wms.api.transaction.util.Chunks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class OrderManagementLineBatchService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    private PickupHeaderV2Repository pickupHeaderV2Repository;

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = Exception.class
    )
    public void saveAndFlushInChunks(List<OrderManagementLineV2> lines) {
        for (List<OrderManagementLineV2> chunk : Chunks.of(lines, 100)) {
            orderManagementLineV2Repository.saveAll(chunk);
            entityManager.flush();
            entityManager.clear();
        }
    }


    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = Exception.class
    )
    public void savePickupAndFlushInChunks(List<PickupHeaderV2> pickupHeaderV2s) {
        for (List<PickupHeaderV2> chunk : Chunks.of(pickupHeaderV2s, 100)) {
            pickupHeaderV2Repository.saveAll(chunk);
            entityManager.flush();
            entityManager.clear();
        }
    }
}