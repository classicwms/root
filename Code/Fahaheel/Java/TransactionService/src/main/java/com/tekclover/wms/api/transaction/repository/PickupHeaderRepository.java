package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;

@Repository
@Transactional
public interface PickupHeaderRepository extends JpaRepository<PickupHeader, Long>,
        JpaSpecificationExecutor<PickupHeader>, StreamableJpaSpecificationRepository<PickupHeader> {
    String UPGRADE_SKIPLOCKED = "-2";

    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "500"))
    public List<PickupHeader> findAll();

    public Optional<PickupHeader> findByPickupNumber(String pickupNumber);

    public PickupHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
            Long lineNumber, String itemCode, Long deletionIndicator);

    public List<PickupHeader> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndOutboundOrderTypeIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long statusId, List<Long> outboundOrderTypeId, Long deletionIndicator);

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
            @Param("warehouseId") String warehouseId, @Param("refDocNumber") String refDocNumber, @Param("preOutboundNo") String preOutboundNo,
            @Param("statusId") Long statusId, @Param("deletionIndicator") long deletionIndicator);


    // Count for MobileDashBoard
    @Query(value = "SELECT COUNT(ref_doc_no) AS count FROM (\n"
            + " select distinct ref_doc_no from \n "
            + " tblpickupheader WHERE \n"
            + "(:languageId IS NULL OR LANG_ID = :languageId) AND \n"
            + "(:companyCode IS NULL OR C_ID = :companyCode) AND \n"
            + "(:plantId IS NULL OR PLANT_ID = :plantId) AND \n"
            + "(:warehouseId IS NULL OR WH_ID = :warehouseId) AND \n"
//            + "(:levelId IS NULL OR LEVEL_ID = :levelId) AND \n"
            + "(STATUS_ID IN (:statusId)) AND \n"
            + "(OB_ORD_TYP_ID IN (:orderTypeId)) AND \n"
            + "(COALESCE(:assignPickerId, null) IS NULL OR (ASS_PICKER_ID IN (:assignPickerId))) AND \n"
//            + "(:orderTypeId IS NULL OR IB_ORD_TYP_ID = :orderTypeId) AND "
            + " IS_DELETED = 0 ) x", nativeQuery = true)
    public Long getPickupHeaderCount(@Param("companyCode") List<String> companyCode,
                                           @Param("plantId") List<String> plantId,
                                           @Param("warehouseId") List<String> warehouseId,
                                           @Param("languageId") List<String> languageId,
//                                           @Param("levelId") String levelId,
                                           @Param("assignPickerId") List<String> assignPickerId,
                                           @Param("statusId") Long statusId,
                                           @Param("orderTypeId") List<Long> orderTypeId);

}