package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface PickupHeaderV2Repository extends JpaRepository<PickupHeaderV2, Long>,
        JpaSpecificationExecutor<PickupHeaderV2>, StreamableJpaSpecificationRepository<PickupHeaderV2> {

    PickupHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
            String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode, Long deletionIndicator);

    @Query("Select count(ob) from PickupHeader ob where ob.companyCodeId=:companyCodeId and ob.plantId=:plantId and ob.languageId=:languageId and ob.warehouseId=:warehouseId and ob.refDocNumber=:refDocNumber and \r\n"
            + " ob.preOutboundNo=:preOutboundNo and ob.statusId = :statusId and ob.deletionIndicator=:deletionIndicator")
    public long getPickupHeaderByWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicatorV2(
            @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId,
            @Param("languageId") String languageId, @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber, @Param("preOutboundNo") String preOutboundNo,
            @Param("statusId") Long statusId, @Param("deletionIndicator") long deletionIndicator);

    PickupHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, String pickupNumber, Long lineNumber, String itemCode, Long deletionIndicator);

    PickupHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndBarcodeIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String barcodeId, Long deletionIndicator);

    PickupHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
            String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String manufacturerName, Long deletionIndicator);

    List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, String pickupNumber, Long lineNumber, String itemCode, Long deletionIndicator);

    PickupHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, String pickupNumber, Long deletionIndicator);

    public List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndOutboundOrderTypeIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long statusId, List<Long> outboundOrderTypeId, Long deletionIndicator);

    public List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String assignedPickerId, Long StatusId, Long deletionIndicator);

    List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdInAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, List<String> assignedPickerId, Long StatusId, Long deletionIndicator);

    List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId,
            String warehouseId, String assignedPickerId, Long deletionIndicator);

    List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, List<String> assignedPickerId, Long deletionIndicator);

    List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long statusId, Long deletionIndicator);

    List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByPickupCreatedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long StatusId, Long deletionIndicator);

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndLevelIdAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long StatusId, String levelId, Long deletionIndicator);

    public List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndLevelIdAndOutboundOrderTypeIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long statusId, String levelId, List<Long> outboundOrderTypeId, Long deletionIndicator);


    List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, String assignedPickerId,
            Long statusId, Date startDate, Date endDate, Long deletionIndicator);

    List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndRefDocNumberAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, String assignedPickerId,
            String refDocNumber, Long statusId, Date startDate, Date endDate, Long deletionIndicator);

    List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdInAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, List<String> assignedPickerId, Long statusId, Date startDate, Date endDate, Long deletionIndicator);

    List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode,
            String manufacturerName, Long statusId, Long deletionIndicator);
    @Transactional
    @Procedure(procedureName = "pickupheader_status_update_proc")
    public void updatePickupheaderStatusUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("itmCode") String itmCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("partnerCode") String partnerCode,
            @Param("pickupNumber") String pickupNumber,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, String assignedPickerId,
            Long statusId, Date startDate, Date endDate, Long deletionIndicator);

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndRefDocNumberAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, String assignedPickerId, String refDocNumber,
            Long statusId, Date startDate, Date endDate, Long deletionIndicator);

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdInAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, List<String> assignedPickerId,
            Long statusId, Date startDate, Date endDate, Long deletionIndicator);

    @Query(value = "select distinct ref_doc_no as refDocNumber, ass_picker_id as assignPicker, wh_id as warehouseId, \n" +
            " ref_doc_type as refDocType from tblpickupheader \n" +
            " WHERE status_id = 48 AND noti_status = 0 AND is_deleted = 0", nativeQuery = true)
    List<IKeyValuePair> findByStatusIdAndNotificationStatusAndDeletionIndicatorDistinctRefDocNo();


    @Query(value = "select distinct ref_doc_no as refDocNumber, ass_picker_id as assignPicker, wh_id as warehouseId, \n" +
            " ref_doc_type as refDocType from tblpickupheader \n" +
            " WHERE status_id = 48 AND noti_status = 0 AND is_deleted = 0 \n" +
            " and pre_ob_no = :preOutboundNo and wh_id = :warehouseId ", nativeQuery = true)
    List<IKeyValuePair> findPushNotificationStatusByPreOutboundNo(@Param("preOutboundNo") String preOutboundNo,
                                                                  @Param("warehouseId") String warehouseId);

    @Query(value = "select distinct ref_doc_no as refDocNumber, ass_picker_id as assignPicker, wh_id as warehouseId, \n" +
            " ref_doc_type as refDocType from tblpickupheader \n" +
            " WHERE status_id = 48 AND noti_status = 0 AND is_deleted = 0 \n" +
            " and pre_ob_no in (:preOutboundNo) and wh_id in (:warehouseId) ", nativeQuery = true)
    List<IKeyValuePair> findPushNotificationStatusByPreOutboundNo(@Param("preOutboundNo") List<String> preOutboundNo,
                                                                  @Param("warehouseId") List<String> warehouseId);


    @Modifying
    @Query(value = "update tblpickupheader set noti_status = 1 where ass_picker_id = :assignPickerId \n" +
            "and wh_id = :warehouseId and ref_doc_no = :refDocNo and is_deleted = 0", nativeQuery = true)
    public void updateNotificationStatus(@Param("assignPickerId") String assignPickerId,
                                         @Param("refDocNo") String refDocNo,
                                         @Param("warehouseId") String warehouseId);


    @Query(value = "select ass_picker_id assignPicker \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "ass_picker_id in \n" +
                    "(select ass_picker_id from \n" +
                    "(select count(pre_ob_no) cnt,ass_picker_id \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "and level_id = :levelId and status_id = :statusId \n" +
                    "and ass_picker_id in (:assignedPickerId) \n" +
                    "group by ass_picker_id \n" +
                    "having count(pre_ob_no) in \n" +
                    "(select top 1 pickerCount from \n" +
                    "(select count(cnt) numb,cnt pickerCount from \n" +
                    "(select count(pre_ob_no) cnt,ass_picker_id \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "and level_id = :levelId and status_id = :statusId \n" +
                    "and ass_picker_id in (:assignedPickerId) \n" +
                    "group by ass_picker_id) x group by cnt) x1)) x2) \n" +
                    "and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "order by pick_ctd_on ", nativeQuery = true)
    public List<String> getPickUpheaderAssignPickerList(@Param("companyCodeId") String companyCodeId,
                                                       @Param("plantId") String plantId,
                                                       @Param("languageId") String languageId,
                                                       @Param("warehouseId") String warehouseId,
                                                       @Param("assignedPickerId") List<String> assignedPickerId,
                                                       @Param("levelId") Long levelId,
                                                       @Param("statusId") Long statusId,
                                                       @Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate);

    @Query(value = "select ass_picker_id assignPicker \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "ass_picker_id in \n" +
                    "(select ass_picker_id from \n" +
                    "(select count(pre_ob_no) cnt,ass_picker_id \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "and ob_ord_typ_id = :outboundOrderTypeId and status_id = :statusId \n" +
                    "and ass_picker_id in (:assignedPickerId) \n" +
                    "group by ass_picker_id \n" +
                    "having count(pre_ob_no) in \n" +
                    "(select top 1 pickerCount from \n" +
                    "(select count(cnt) numb,cnt pickerCount from \n" +
                    "(select count(pre_ob_no) cnt,ass_picker_id \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "and ob_ord_typ_id = :outboundOrderTypeId and status_id = :statusId \n" +
                    "and ass_picker_id in (:assignedPickerId) \n" +
                    "group by ass_picker_id) x group by cnt) x1)) x2) \n" +
                    "and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "order by pick_ctd_on ", nativeQuery = true)
    public List<String> getPickUpheaderAssignPickerAmgharaList(@Param("companyCodeId") String companyCodeId,
                                                               @Param("plantId") String plantId,
                                                               @Param("languageId") String languageId,
                                                               @Param("warehouseId") String warehouseId,
                                                               @Param("assignedPickerId") List<String> assignedPickerId,
                                                               @Param("outboundOrderTypeId") Long outboundOrderTypeId,
                                                               @Param("statusId") Long statusId,
                                                               @Param("startDate") Date startDate,
                                                               @Param("endDate") Date endDate);

    @Query(value = "select ass_picker_id assignPicker \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "ass_picker_id in \n" +
                    "(select ass_picker_id from \n" +
                    "(select count(pre_ob_no) cnt,ass_picker_id \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "and level_id = :levelId and status_id = :statusId \n" +
                    "and ass_picker_id in (:assignedPickerId) \n" +
                    "group by ass_picker_id \n" +
                    "having count(pre_ob_no) in \n" +
                    "(select top 1 pickerCount from \n" +
                    "(select count(cnt) numb,cnt pickerCount from \n" +
                    "(select count(pre_ob_no) cnt,ass_picker_id \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "and level_id = :levelId and status_id = :statusId \n" +
                    "and ass_picker_id in (:assignedPickerId) \n" +
                    "group by ass_picker_id) x group by cnt) x1)) x2) \n" +
                    "and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "order by pick_ctd_on ", nativeQuery = true)
    public List<String> getPickUpheaderAssignPickerListNew(@Param("companyCodeId") String companyCodeId,
                                                           @Param("plantId") String plantId,
                                                           @Param("languageId") String languageId,
                                                           @Param("warehouseId") String warehouseId,
                                                           @Param("assignedPickerId") String assignedPickerId,
                                                           @Param("levelId") Long levelId,
                                                           @Param("statusId") Long statusId,
                                                           @Param("startDate") Date startDate,
                                                           @Param("endDate") Date endDate);

    @Query(value = "select ass_picker_id assignPicker \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "ass_picker_id in \n" +
                    "(select ass_picker_id from \n" +
                    "(select count(pre_ob_no) cnt,ass_picker_id \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "and ob_ord_typ_id = :outboundOrderTypeId and status_id = :statusId \n" +
                    "and ass_picker_id in (:assignedPickerId) \n" +
                    "group by ass_picker_id \n" +
                    "having count(pre_ob_no) in \n" +
                    "(select top 1 pickerCount from \n" +
                    "(select count(cnt) numb,cnt pickerCount from \n" +
                    "(select count(pre_ob_no) cnt,ass_picker_id \n" +
                    "from tblpickupheader \n" +
                    "where \n" +
                    "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "and ob_ord_typ_id = :outboundOrderTypeId and status_id = :statusId \n" +
                    "and ass_picker_id in (:assignedPickerId) \n" +
                    "group by ass_picker_id) x group by cnt) x1)) x2) \n" +
                    "and \n" +
                    "pick_ctd_on between :startDate and :endDate \n" +
                    "order by pick_ctd_on ", nativeQuery = true)
    public List<String> getPickUpheaderAssignPickerAmgharaListNew(@Param("companyCodeId") String companyCodeId,
                                                                  @Param("plantId") String plantId,
                                                                  @Param("languageId") String languageId,
                                                                  @Param("warehouseId") String warehouseId,
                                                                  @Param("assignedPickerId") String assignedPickerId,
                                                                  @Param("outboundOrderTypeId") Long outboundOrderTypeId,
                                                                  @Param("statusId") Long statusId,
                                                                  @Param("startDate") Date startDate,
                                                                  @Param("endDate") Date endDate);

    @Query(value = "select count(pre_ob_no) pickerCount,ass_picker_id assignPicker \n" +
            " from tblpickupheader ob where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and level_id = :levelId and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 \n" +
            " group by ass_picker_id order by pickerCount ", nativeQuery = true)
    public List<IKeyValuePair> getAssignPicker(@Param("companyCodeId") String companyCodeId,
                                               @Param("plantId") String plantId,
                                               @Param("languageId") String languageId,
                                               @Param("warehouseId") String warehouseId,
                                               @Param("assignedPickerId") List<String> assignedPickerId,
                                               @Param("levelId") Long levelId,
                                               @Param("statusId") Long statusId,
                                               @Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate);
    @Query(value = "select ass_picker_id assignPicker \n" +
            " from tblpickupheader ob where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and level_id = :levelId and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 \n" +
            " order by pick_ctd_on ", nativeQuery = true)
    public List<IKeyValuePair> getAssignPickerList(@Param("companyCodeId") String companyCodeId,
                                                   @Param("plantId") String plantId,
                                                   @Param("languageId") String languageId,
                                                   @Param("warehouseId") String warehouseId,
                                                   @Param("assignedPickerId") List<String> assignedPickerId,
                                                   @Param("levelId") Long levelId,
                                                   @Param("statusId") Long statusId,
                                                   @Param("startDate") Date startDate,
                                                   @Param("endDate") Date endDate);
    @Query(value = "select top 1 count(pre_ob_no) cnt,ass_picker_id assignPicker \n" +
            " from tblpickupheader ob where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and level_id = :levelId and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 \n" +
            " group by ass_picker_id order by cnt ", nativeQuery = true)
    public IKeyValuePair getAssignPickerNew(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("languageId") String languageId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("assignedPickerId") List<String> assignedPickerId,
                                            @Param("levelId") Long levelId,
                                            @Param("statusId") Long statusId,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);

    @Query(value = "select ass_picker_id \n" +
            " from tblpickupheader ob where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and level_id = :levelId and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 order by pick_ctd_on", nativeQuery = true)
    public List<String> getPickUpheader50AssignPickerList(@Param("companyCodeId") String companyCodeId,
                                                         @Param("plantId") String plantId,
                                                         @Param("languageId") String languageId,
                                                         @Param("warehouseId") String warehouseId,
                                                         @Param("assignedPickerId") List<String> assignedPickerId,
                                                         @Param("levelId") Long levelId,
                                                         @Param("statusId") Long statusId,
                                                         @Param("startDate") Date startDate,
                                                         @Param("endDate") Date endDate);

    @Query(value = "select ass_picker_id \n" +
            " from tblpickupheader ob where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and ob_ord_typ_id = :outboundOrderTypeId and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 order by pick_ctd_on", nativeQuery = true)
    public List<String> getPickUpheader50AssignPickerObTypeList(@Param("companyCodeId") String companyCodeId,
                                                                @Param("plantId") String plantId,
                                                                @Param("languageId") String languageId,
                                                                @Param("warehouseId") String warehouseId,
                                                                @Param("assignedPickerId") List<String> assignedPickerId,
                                                                @Param("outboundOrderTypeId") Long outboundOrderTypeId,
                                                                @Param("statusId") Long statusId,
                                                                @Param("startDate") Date startDate,
                                                                @Param("endDate") Date endDate);

    @Query(value = "select top 1 count(pre_ob_no) cnt,ass_picker_id assignPicker \n" +
            " from tblpickupheader ob \n" +
            " join tblordertypeid ot on ot.usr_id = ob.ass_picker_id \n" +
            " where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and ot.ob_ord_typ_id in (:outboundOrderTypeId) and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 \n" +
            " group by ass_picker_id order by cnt" ,nativeQuery = true)
    public IKeyValuePair getAssignPickerWh100New(@Param("companyCodeId") String companyCodeId,
                                                 @Param("plantId") String plantId,
                                                 @Param("languageId") String languageId,
                                                 @Param("warehouseId") String warehouseId,
                                                 @Param("assignedPickerId") List<String> assignedPickerId,
                                                 @Param("statusId") Long statusId,
                                                 @Param("outboundOrderTypeId") Long outboundOrderTypeId,
                                                 @Param("startDate") Date startDate,
                                                 @Param("endDate") Date endDate);
    @Query(value = "select count(pre_ob_no) pickerCount,ass_picker_id assignPicker \n" +
            " from tblpickupheader ob \n" +
            " join tblordertypeid ot on ot.usr_id = ob.ass_picker_id \n" +
            " where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and ot.ob_ord_typ_id in (:outboundOrderTypeId) and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 \n" +
            " group by ass_picker_id order by pickerCount" ,nativeQuery = true)
    public List<IKeyValuePair> getAssignPickerWh100(@Param("companyCodeId") String companyCodeId,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("warehouseId") String warehouseId,
                                                    @Param("assignedPickerId") List<String> assignedPickerId,
                                                    @Param("statusId") Long statusId,
                                                    @Param("outboundOrderTypeId") Long outboundOrderTypeId,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate);

    @Query(value = "select ass_picker_id assignPicker \n" +
            " from tblpickupheader ob \n" +
            " join tblordertypeid ot on ot.usr_id = ob.ass_picker_id \n" +
            " where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and ot.ob_ord_typ_id in (:outboundOrderTypeId) and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 \n" +
            " order by pick_ctd_on ", nativeQuery = true)
    public List<IKeyValuePair> getAssignPickerW100List(@Param("companyCodeId") String companyCodeId,
                                                       @Param("plantId") String plantId,
                                                       @Param("languageId") String languageId,
                                                       @Param("warehouseId") String warehouseId,
                                                       @Param("assignedPickerId") List<String> assignedPickerId,
                                                       @Param("statusId") Long statusId,
                                                       @Param("outboundOrderTypeId") Long outboundOrderTypeId,
                                                       @Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate);

    @Query(value = "select distinct concat(itm_code,mfr_name) \n" +
            " from tblpickupheader ob where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
//            " ob.ass_picker_id in (:assignedPickerId) and \n" +
            " ob.status_id = :statusId and \n" +
//            " level_id = :levelId and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 ", nativeQuery = true)
    public List<String> getPickupHeaderAssignPickerList(@Param("companyCodeId") String companyCodeId,
                                                        @Param("plantId") String plantId,
                                                        @Param("languageId") String languageId,
                                                        @Param("warehouseId") String warehouseId,
                                                        @Param("statusId") Long statusId,
                                                        @Param("startDate") Date startDate,
                                                        @Param("endDate") Date endDate);


    @Query(value = "select token_id as tokenId from tblhhtnotification where usr_id = :userId and \n" +
            " wh_id = :warehouseId and is_deleted = 0", nativeQuery = true)
    public List<String> getDeviceToken(@Param("userId")String userId,
                                       @Param("warehouseId")String warehouseId);

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndOutboundOrderTypeIdAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode,
            String manufacturerName, Long statusId, Long outboundOrderTypeId, Long deletionIndicator);

	public List<PickupHeaderV2> findByPickupNumberAndDeletionIndicator(String pickupNumber, long l);

    //===================================================Walkaroo-v3======================================================================================

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String itemCode, String manufacturerName, Long statusId, Long deletionIndicator);

    @Query(value = "select ass_picker_id into #tph \n" +
            " from tblpickupheader ob where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 order by pick_ctd_on \n" +
            " select ass_picker_id from #tph group by ass_picker_id", nativeQuery = true)
    public List<String> getPickUpheader50AssignPickerListV3(@Param("companyCodeId") String companyCodeId,
                                                            @Param("plantId") String plantId,
                                                            @Param("languageId") String languageId,
                                                            @Param("warehouseId") String warehouseId,
                                                            @Param("assignedPickerId") List<String> assignedPickerId,
                                                            @Param("statusId") Long statusId,
                                                            @Param("startDate") Date startDate,
                                                            @Param("endDate") Date endDate);

    @Query(value = "select ass_picker_id assignPicker \n" +
            "from tblpickupheader \n" +
            "where \n" +
            "ass_picker_id in \n" +
            "(select ass_picker_id from \n" +
            "(select count(pre_ob_no) cnt,ass_picker_id \n" +
            "from tblpickupheader \n" +
            "where \n" +
            "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
            "pick_ctd_on between :startDate and :endDate \n" +
            "and status_id = :statusId \n" +
            "and ass_picker_id in (:assignedPickerId) \n" +
            "group by ass_picker_id \n" +
            "having count(pre_ob_no) in \n" +
            "(select top 1 pickerCount from \n" +
            "(select count(cnt) numb,cnt pickerCount from \n" +
            "(select count(pre_ob_no) cnt,ass_picker_id \n" +
            "from tblpickupheader \n" +
            "where \n" +
            "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
            "pick_ctd_on between :startDate and :endDate \n" +
            "and status_id = :statusId \n" +
            "and ass_picker_id in (:assignedPickerId) \n" +
            "group by ass_picker_id) x group by cnt) x1)) x2) \n" +
            "and \n" +
            "pick_ctd_on between :startDate and :endDate \n" +
            "order by pick_ctd_on ", nativeQuery = true)
    public List<String> getPickUpheaderAssignPickerListV3(@Param("companyCodeId") String companyCodeId,
                                                          @Param("plantId") String plantId,
                                                          @Param("languageId") String languageId,
                                                          @Param("warehouseId") String warehouseId,
                                                          @Param("assignedPickerId") String assignedPickerId,
                                                          @Param("statusId") Long statusId,
                                                          @Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate);

    @Query(value = "select top 1 count(pre_ob_no) cnt,ass_picker_id assignPicker \n" +
            " from tblpickupheader ob where ob.c_id=:companyCodeId and ob.plant_id=:plantId and ob.lang_Id=:languageId and ob.wh_id=:warehouseId and \n" +
            " ob.ass_picker_id in (:assignedPickerId) and ob.status_id = :statusId and \r\n" +
            " ob.pick_ctd_on between :startDate and :endDate and ob.ass_picker_id is not null and ob.is_deleted = 0 \n" +
            " group by ass_picker_id order by cnt ", nativeQuery = true)
    public IKeyValuePair getAssignPickerV3(@Param("companyCodeId") String companyCodeId,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("warehouseId") String warehouseId,
                                            @Param("assignedPickerId") List<String> assignedPickerId,
                                            @Param("statusId") Long statusId,
                                            @Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate);

    @Query(value = "select ass_picker_id assignPicker \n" +
            "from tblpickupheader \n" +
            "where \n" +
            "ass_picker_id in \n" +
            "(select ass_picker_id from \n" +
            "(select count(pre_ob_no) cnt,ass_picker_id \n" +
            "from tblpickupheader \n" +
            "where \n" +
            "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
            "pick_ctd_on between :startDate and :endDate \n" +
            "and status_id = :statusId \n" +
            "and ass_picker_id in (:assignedPickerId) \n" +
            "group by ass_picker_id \n" +
            "having count(pre_ob_no) in \n" +
            "(select top 1 pickerCount from \n" +
            "(select count(cnt) numb,cnt pickerCount from \n" +
            "(select count(pre_ob_no) cnt,ass_picker_id \n" +
            "from tblpickupheader \n" +
            "where \n" +
            "c_id=:companyCodeId and plant_id=:plantId and lang_Id=:languageId and wh_id=:warehouseId and \n" +
            "pick_ctd_on between :startDate and :endDate \n" +
            "and status_id = :statusId \n" +
            "and ass_picker_id in (:assignedPickerId) \n" +
            "group by ass_picker_id) x group by cnt) x1)) x2) \n" +
            "and \n" +
            "pick_ctd_on between :startDate and :endDate \n" +
            "order by pick_ctd_on ", nativeQuery = true)
    public List<String> getPickUpheaderAssignPickerListV3(@Param("companyCodeId") String companyCodeId,
                                                          @Param("plantId") String plantId,
                                                          @Param("languageId") String languageId,
                                                          @Param("warehouseId") String warehouseId,
                                                          @Param("assignedPickerId") List<String> assignedPickerId,
                                                          @Param("statusId") Long statusId,
                                                          @Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate);

    public PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndCustomerIdAndDeletionIndicatorOrderByPickupCreatedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String customerId, Long deletionIndicator);

    List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndCustomerIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String customerId, Long deletionIndicator);

    List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String itemCode, Long deletionIndicator);

    List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndItemCodeAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String itemCode, Long statusId, Long deletionIndicator);

    List<PickupHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCustomerIdAndAssignedPickerIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String customerId, String assignedPickerId, Date startDate, Date endDate, Long deletionIndicator);

	PickupHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndBarcodeIdAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
			String refDocNumber, String partnerCode, String pickupNumber, String barcodeId, Long deletionIndicator);

    Optional<PickupHeaderV2> findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndBarcodeIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
            String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String proposedStorageBin,
            String proposedPackBarCode, String barcodeId, Long deletionIndicator);

    List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndSalesOrderNumberAndDeletionIndicatorOrderByPickupNumber(
            String companyCodeId, String plantId, String languageId, String warehouseId, String salesOrderNumber, Long deletionIndicator);

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndCustomerIdAndItemCodeAndBarcodeIdAndDeletionIndicatorOrderByPickupCreatedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String customerId, String itemCode, String barcodeId, Long deletionIndicator);

    boolean existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPickupNumberAndBarcodeIdAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String pickupNumber, String barcodeId, Long statusId, Long deletionIndicator);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE tblpickupheader SET ASS_PICKER_ID = :assignedUserId \n" +
            "WHERE PU_NO = :pickupNumber", nativeQuery = true)
    void updateAssignPicker(@Param("pickupNumber") String pickupNumber,
                            @Param("assignedUserId") String assignedUserId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tblpickupheader set noti_status = 1 where \n" +
            "wh_id = :warehouseId and ref_doc_no = :refDocNo and is_deleted = 0", nativeQuery = true)
    public void updateNotificationStatus(@Param("refDocNo") String refDocNo,
                                         @Param("warehouseId") String warehouseId);

    @Query(value = "select token_id from tblhhtnotification where \n" +
            "c_id = :companyId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and is_deleted = 0", nativeQuery = true)
    public List<String> getDeviceToken(@Param("companyId")String companyId,
                                       @Param("plantId")String plantId,
                                       @Param("languageId")String languageId,
                                       @Param("warehouseId")String warehouseId);

    boolean existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long statusId,  Long deletionIndicator);

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndCustomerIdAndItemCodeAndBarcodeIdAndDeletionIndicatorOrderByPickupCreatedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber, String customerId, String itemCode, String barcodeId, Long deletionIndicator);

    List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long statusId,  Long deletionIndicator);

    List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber, String itemCode, Long deletionIndicator);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = " UPDATE tblpickupheader SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription \n" +
            " WHERE c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId " +
            " and is_deleted = 0 and PU_NO = :pickupNumber and PARTNER_ITEM_BARCODE = :barcodeId and REF_DOC_NO = :refDocNumber AND STATUS_ID <> :statusId", nativeQuery = true)
    void updateHeaderStatus(@Param("companyCodeId") String companyCodeId,
                            @Param("plantId") String plantId,
                            @Param("languageId") String languageId,
                            @Param("warehouseId") String warehouseId,
                            @Param("refDocNumber") String refDocNumber,
                            @Param("statusId") Long statusId,
                            @Param("statusDescription") String statusDescription,
                            @Param("pickupNumber") String pickupNumber,
                            @Param("barcodeId") String barcodeId);

    @Query(value = "select token_id from tblhhtnotification where \n" +
            "c_id = :companyId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and is_deleted = 0 and prtl_usr = :status ", nativeQuery = true)
    public List<String> getDeviceTokenV3(@Param("companyId") String companyId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("status") boolean status);
    
    //============================PGIReversal================================================================
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblpickupheader SET STATUS_ID = :statusId, REF_FIELD_10 = :statusDescription, STATUS_TEXT = :statusDescription \n" +
            "WHERE LANG_ID = :languageId AND C_ID = :companyCodeId AND \n" +
            "PLANT_ID = :plantId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND ITM_CODE = :itemCode", nativeQuery = true)
    void updatePickupHeaderStatusV3(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("refDocNumber") String refDocNumber,
                                         @Param("itemCode") String itemCode,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);
    
    @Modifying
    @Query(value = "UPDATE tblpickupheader SET is_deleted = 1 where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber AND pre_ob_no = :preOutboundNo " +
            "AND is_deleted = 0", nativeQuery = true)
    void deletePickupHeader (@Param("companyCodeId") String companyCodeId,
                                   @Param("plantId") String plantId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("preOutboundNo") String preOutboundNo);
    
    boolean existsByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

    boolean existsByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, Long deletionIndicator);


    @Modifying()
    @Query(value = "Update tblpickupheader set ref_field_2 = :refField2, PICK_UTD_BY = :updatedBy, PICK_UTD_ON = getDate() " +
            "Where c_id = :companyCodeId and plant_id = :plantId and wh_id = :warehouseId and ref_doc_no = :refDocNumber and pre_ob_no = :preOutboundNo " +
            "and PU_NO = :pickupNumber and is_deleted = 0", nativeQuery = true)
    void updatedPickupHeader(@Param("companyCodeId") String companyCodeId,
                             @Param("plantId") String plantId,
                             @Param("warehouseId") String warehouseId,
                             @Param("refDocNumber") String refDocNumber,
                             @Param("preOutboundNo") String preOutboundNo,
                             @Param("pickupNumber") String pickupNumber,
                             @Param("refField2") String refField2,
                             @Param("updatedBy") String updatedBy);
}