package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.outboundorderstatusid.OutboundOrderStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface OutboundOrderStatusIdRepository extends JpaRepository<OutboundOrderStatusId,Long>, JpaSpecificationExecutor<OutboundOrderStatusId> {
	
	public List<OutboundOrderStatusId> findAll();
	public Optional<OutboundOrderStatusId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndOutboundOrderStatusIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String outboundOrderStatusId, String languageId, Long deletionIndicator);
}