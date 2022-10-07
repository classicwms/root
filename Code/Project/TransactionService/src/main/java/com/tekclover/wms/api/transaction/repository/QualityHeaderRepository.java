package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;

@Repository
@Transactional
public interface QualityHeaderRepository extends JpaRepository<QualityHeader,Long>, JpaSpecificationExecutor<QualityHeader> {
	
	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize", value="1000"))
	public List<QualityHeader> findAll();

	public Optional<QualityHeader> findByQualityInspectionNo(String qualityInspectionNo);
	
	public QualityHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String qualityInspectionNo,
			String actualHeNo, Long deletionIndicator);
	
	
	public List<QualityHeader> findByWarehouseIdAndStatusId (String warehouseId, Long statusId);

	public QualityHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPickupNumberAndPartnerCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String pickupNumber, String partnerCode,
			Long l);

	public QualityHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			String qualityInspectionNo, String actualHeNo, long l);

	public List<QualityHeader> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo,
			Long l);
}