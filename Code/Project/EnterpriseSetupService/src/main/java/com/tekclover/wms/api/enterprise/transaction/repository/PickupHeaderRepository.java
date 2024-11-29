package com.tekclover.wms.api.enterprise.transaction.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PickupHeaderRepository
		extends JpaRepository<PickupHeader, Long>,
				JpaSpecificationExecutor<PickupHeader>,
		StreamableJpaSpecificationRepository<PickupHeader> {
	String UPGRADE_SKIPLOCKED = "-2";
	
	@QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "500"))
	public List<PickupHeader> findAll();

	public Optional<PickupHeader> findByPickupNumber(String pickupNumber);

	public PickupHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			Long lineNumber, String itemCode, Long deletionIndicator);

	public List<PickupHeader> findByWarehouseIdAndStatusIdAndOutboundOrderTypeIdInAndDeletionIndicator(
			String warehouseId, Long statusId, List<Long> outboundOrderTypeId, Long deletionIndicator);

	@Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = UPGRADE_SKIPLOCKED)})
	public PickupHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			Long deletionIndicator);

	public List<PickupHeader> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			Long lineNumber, String itemCode, Long deletionIndicator);

	public PickupHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, String proposedStorageBin, String proposedPackCode, Long deletionIndicator);

	@Query("Select count(ob) from PickupHeader ob where ob.warehouseId=:warehouseId and ob.refDocNumber=:refDocNumber and \r\n"
			+ " ob.preOutboundNo=:preOutboundNo and ob.statusId = :statusId and ob.deletionIndicator=:deletionIndicator")
	public long getPickupHeaderByWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(
			 @Param ("warehouseId") String warehouseId, @Param ("refDocNumber") String refDocNumber, @Param ("preOutboundNo") String preOutboundNo, 
			 @Param ("statusId") Long statusId, @Param ("deletionIndicator") long deletionIndicator);
}