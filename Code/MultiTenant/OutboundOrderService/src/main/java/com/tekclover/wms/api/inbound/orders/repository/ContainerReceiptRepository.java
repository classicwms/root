package com.tekclover.wms.api.inbound.orders.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.tekclover.wms.api.inbound.orders.model.inbound.containerreceipt.ContainerReceipt;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface ContainerReceiptRepository extends JpaRepository<ContainerReceipt,Long>,
		JpaSpecificationExecutor<ContainerReceipt>, StreamableJpaSpecificationRepository<ContainerReceipt> {
	
	/**
	 * 
	 */
	public List<ContainerReceipt> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param containerReceiptNo
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<ContainerReceipt> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndContainerReceiptNoAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, 
				String preInboundNo, String refDocNumber, String containerReceiptNo, Long deletionIndicator);

	public Optional<ContainerReceipt> findByPreInboundNo(String containerReceiptNo);

	public Optional<ContainerReceipt> findByContainerReceiptNoAndDeletionIndicator(String containerReceiptNo, Long deletionIndicator);

	long countByWarehouseIdAndContainerReceivedDateBetweenAndRefDocNumberIsNull(String warehouseId, Date fromDate, Date toDate);
}