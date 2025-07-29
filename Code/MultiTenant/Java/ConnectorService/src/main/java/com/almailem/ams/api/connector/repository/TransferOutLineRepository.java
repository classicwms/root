package com.almailem.ams.api.connector.repository;


import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
import com.almailem.ams.api.connector.model.transferout.TransferOutLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TransferOutLineRepository extends JpaRepository<TransferOutLine, String>,
        JpaSpecificationExecutor<TransferOutLine> {


}
