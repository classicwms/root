package com.tekclover.wms.api.idmaster.repository.enterprise;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.enterprise.WarehouseEnterprise;


@Repository
@Transactional
public interface WarehouseEnterpriseRepository extends JpaRepository<WarehouseEnterprise,Long>, JpaSpecificationExecutor<WarehouseEnterprise> {

	public List<WarehouseEnterprise> findByZone(String zone);
}