package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;

@Repository
@Transactional
public interface OutboundHeaderRepository extends JpaRepository<OutboundHeader,Long>, JpaSpecificationExecutor<OutboundHeader> {
	
	public List<OutboundHeader> findAll();
	
	public Optional<OutboundHeader> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, 
				String refDocNumber, String partnerCode, Long deletionIndicator);
	
	public Optional<OutboundHeader> findByPreOutboundNo(String preOutboundNo);
	
	public OutboundHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long deletionIndicator);

	public OutboundHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String referenceField2, Long deletionIndicator);

	public OutboundHeader findByRefDocNumberAndDeletionIndicator(String refDocNumber, long l);

	/*
	 * Reports
	 */
//	@Query (value = "SELECT COUNT(REF_DOC_NO) FROM tbloutboundheader \r\n"
//			+ " WHERE WH_ID = :warehouseId AND PARTNER_CODE = :partnerCode AND REF_FIELD_1 = :refField1 \r\n"
//			+ " AND DLV_CNF_ON BETWEEN :startDate AND :endDate AND STATUS_ID = 59 \r\n"
//			+ " GROUP BY REF_DOC_NO", nativeQuery = true)
//	public List<Long> findTotalOrder_NByWarehouseIdAndPartnerCode (
//									@Param(value = "warehouseId") String warehouseId,
//									@Param(value = "partnerCode") String partnerCode,
//									@Param(value = "refField1") String refField1,
//									@Param ("startDate") Date startDate,
//									@Param ("endDate") Date endDate);
	
	public List<OutboundHeader> findByWarehouseIdAndPartnerCodeAndReferenceField1AndStatusIdAndDeliveryConfirmedOnBetween (
			String warehouseId, String partnerCode, String refField1, Long statusId, Date startDate, Date endDate);
	
//	@Query (value = "SELECT REF_DOC_NO AS refDocNo FROM tbloutboundheader \r\n"
//			+ " WHERE WH_ID = :warehouseId AND PARTNER_CODE = :partnerCode \r\n"
//			+ " AND STATUSI_ID = 59 AND DLV_CNF_ON BETWEEN :startDate AND :endDate \r\n"
//			+ " GROUP BY REF_DOC_NO", nativeQuery = true)
//	public List<String> findRefDocNoByWarehouseIdAndPartnerCode (
//									@Param(value = "warehouseId") String warehouseId,
//									@Param(value = "partnerCode") String partnerCode,
//									@Param ("startDate") Date startDate,
//									@Param ("endDate") Date endDate);
	
	public List<OutboundHeader> findByWarehouseIdAndStatusIdAndDeliveryConfirmedOnBetween (String warehouseId,Long statusId, Date startDate, Date endDate);
	public List<OutboundHeader> findByStatusIdAndPartnerCodeAndDeliveryConfirmedOnBetween (Long statusId, 
			String partnerCode, Date startDate, Date endDate);
	
//	public List<OutboundHeader> findByStatusIdAndPartnerCodeAndDeliveryConfirmedOnBetweenAndReferenceField1 (Long statusId, 
//			String partnerCode, Date startDate, Date endDate, String refField1);
}