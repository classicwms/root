package com.ustorage.api.trans.repository;

import com.ustorage.api.trans.model.itemservice.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemServiceRepository extends JpaRepository<ItemService, Long>,
        JpaSpecificationExecutor<ItemService>{

    public List<ItemService> findAll();

    List<ItemService> findByWorkOrderIdAndDeletionIndicator(String workOrderId, long l);

}
