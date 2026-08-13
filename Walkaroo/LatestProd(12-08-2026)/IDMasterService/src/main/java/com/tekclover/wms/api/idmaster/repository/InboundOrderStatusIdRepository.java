package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.inboundorderstatusid.InboundOrderStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface InboundOrderStatusIdRepository extends JpaRepository<InboundOrderStatusId,Long>, JpaSpecificationExecutor<InboundOrderStatusId> {
	
	public List<InboundOrderStatusId> findAll();
	public Optional<InboundOrderStatusId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInboundOrderStatusIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String inboundOrderStatusId, String languageId, Long deletionIndicator);
}