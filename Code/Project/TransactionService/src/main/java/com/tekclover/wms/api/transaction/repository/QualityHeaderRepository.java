package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface QualityHeaderRepository extends JpaRepository<QualityHeader,Long>,
													JpaSpecificationExecutor<QualityHeader>,
													StreamableJpaSpecificationRepository<QualityHeader> {
	
	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize", value="1000"))
	public List<QualityHeader> findAll();

	public Optional<QualityHeader> findByQualityInspectionNo(String qualityInspectionNo);
	
	public Optional<QualityHeader> findByQualityInspectionNoAndRefDocNumberAndReferenceField4(String qualityInspectionNo,
			String refDocNumber, String itemCode);
	
	public QualityHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String qualityInspectionNo,
			String actualHeNo, Long deletionIndicator);
	
	public List<QualityHeader> findByWarehouseIdAndStatusIdAndDeletionIndicator (String warehouseId, Long statusId, Long deletionIndicator);

	public List<QualityHeader> findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPickupNumberAndPartnerCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String pickupNumber, String partnerCode,
			Long l);

	public QualityHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			String qualityInspectionNo, String actualHeNo, long l);

	public List<QualityHeader> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo,
			Long l);
	
	@Query("Select count(ob) from QualityHeader ob where ob.warehouseId=:warehouseId and ob.refDocNumber=:refDocNumber and \r\n"
			+ " ob.preOutboundNo=:preOutboundNo and ob.statusId = :statusId and ob.deletionIndicator=:deletionIndicator")
	public long getQualityHeaderByWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(
			 @Param ("warehouseId") String warehouseId, @Param ("refDocNumber") String refDocNumber, @Param ("preOutboundNo") String preOutboundNo, 
			 @Param ("statusId") Long statusId, @Param ("deletionIndicator") long deletionIndicator);
	
	/*
	 * updateQualityHeader.setStatusId(55L);
		updateQualityHeader.setReferenceField10(idStatus.getStatus());
	 */
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE QualityHeader qh SET qh.statusId = 55, qh.referenceField10 = :referenceField10 \r\n"
			+ " WHERE qh.qualityInspectionNo = :qualityInspectionNo")
	public void updateQualityHeader (@Param ("referenceField10") String referenceField10,
			@Param ("qualityInspectionNo") String qualityInspectionNo);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE QualityHeader qh SET qh.statusId = :statusId, qh.referenceField10 = :referenceField10, qh.qualityReversedBy = :qualityReversedBy \r\n"
			+ " WHERE qh.qualityInspectionNo = :qualityInspectionNo")
	public void updateQualityHeaderReversal (@Param ("statusId") Long statusId,
											 @Param ("referenceField10") String referenceField10,
											 @Param ("referenceField10") String qualityReversedBy,
											 @Param ("qualityInspectionNo") String qualityInspectionNo);
}