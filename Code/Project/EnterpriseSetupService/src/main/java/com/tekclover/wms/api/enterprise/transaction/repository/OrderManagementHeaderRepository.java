package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.OrderManagementHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface OrderManagementHeaderRepository extends JpaRepository<OrderManagementHeader,Long>, JpaSpecificationExecutor<OrderManagementHeader> {
	
	public List<OrderManagementHeader> findAll();
	
	public Optional<OrderManagementHeader> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long deletionIndicator);
	
	public Optional<OrderManagementHeader> findByRefDocNumber(String refDocNumber);
	
	public OrderManagementHeader findByPreOutboundNo(String preOutboundNo);

	public OrderManagementHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, long l);
}