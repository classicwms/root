package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;

@Repository
@Transactional
public interface PickupLineRepository extends JpaRepository<PickupLine,Long>, JpaSpecificationExecutor<PickupLine> {
	
	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize", value="1000"))
	public List<PickupLine> findAll();
	
	public Optional<PickupLine> findByActualHeNo(String actualHeNo);

	public PickupLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, Long deletionIndicator);

	public PickupLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndActualHeNoAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String pickupNumber, String itemCode, String pickedStorageBin, String pickedPackCode, String actualHeNo,
			long l);

	public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, Long deletionIndicator);

	public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndPickedPackCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode,String pickedPackCode, Long deletionIndicator);
	
	@Query(value="SELECT SUM(PICK_CNF_QTY) FROM tblpickupline WHERE WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND\r\n"
			+ "PRE_OB_NO = :preOutboundNo AND OB_LINE_NO = :obLineNumber AND ITM_CODE = :itemCode AND IS_DELETED = 0 \r\n"
			+ "GROUP BY REF_DOC_NO", nativeQuery=true)
    public Double getPickupLineCount(@Param ("warehouseId") String warehouseId,
    									@Param ("refDocNumber") String refDocNumber,
    									@Param ("preOutboundNo") String preOutboundNo,
    									@Param ("obLineNumber") Long obLineNumber,
    									@Param ("itemCode") String itemCode);

	public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> lineNumbers,
			List<String> itemCodes, long l); 
}