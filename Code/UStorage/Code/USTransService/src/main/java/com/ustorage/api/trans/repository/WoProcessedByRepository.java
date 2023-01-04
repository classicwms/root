package com.ustorage.api.trans.repository;

import com.ustorage.api.trans.model.workorder.WoProcessedBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WoProcessedByRepository extends JpaRepository<WoProcessedBy, Long>,
        JpaSpecificationExecutor<WoProcessedBy>{

    public List<WoProcessedBy> findAll();

    List<WoProcessedBy> findByWorkOrderIdAndDeletionIndicator(String workOrderId, long l);

}
