package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.v2.ContainerReceiptV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.ContainerReceipt;

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

    @Query(value = "select * from tblcontainerreceipt where c_id = :companyCodeId and lang_id = :languageId and plant_id = :plantId and wh_id = :warehouseId and INV_NO = :invoiceNo and \n" +
            "is_deleted=0", nativeQuery = true)
    public ContainerReceiptV2 getContainerReceipt(@Param(value = "companyCodeId") String companyCodeId,
                                                  @Param(value = "languageId") String languageId,
                                                  @Param(value = "plantId") String plantId,
                                                  @Param(value = "warehouseId") String warehouseId,
                                                  @Param(value = "invoiceNo") String invoiceNo);

}