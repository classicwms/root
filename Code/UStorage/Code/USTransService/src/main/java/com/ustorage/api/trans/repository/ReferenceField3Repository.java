package com.ustorage.api.trans.repository;

import com.ustorage.api.trans.model.paymentvoucher.ReferenceField3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferenceField3Repository extends JpaRepository<ReferenceField3, Long>,
        JpaSpecificationExecutor<ReferenceField3>{

    public List<ReferenceField3> findAll();

    List<ReferenceField3> findByVoucherIdAndDeletionIndicator(String voucherId, long l);

}
