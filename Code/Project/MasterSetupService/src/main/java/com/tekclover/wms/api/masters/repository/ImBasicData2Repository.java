package com.tekclover.wms.api.masters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.imbasicdata2.ImBasicData2;

@Repository
@Transactional
public interface ImBasicData2Repository extends JpaRepository<ImBasicData2,Long>, JpaSpecificationExecutor<ImBasicData2> {

	Optional<ImBasicData2> findByItemCode(String itemCode);
}