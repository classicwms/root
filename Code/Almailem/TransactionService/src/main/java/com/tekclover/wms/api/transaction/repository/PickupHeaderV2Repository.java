package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    PickupHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByPickupCreatedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long StatusId, Long deletionIndicator);

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

    @Query(value = "select count(ref_doc_no) pickerCount,ass_picker_id assignPicker \n" +
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
    @Query(value = "select top 1 count(ref_doc_no) cnt,ass_picker_id assignPicker \n" +
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
    @Query(value = "select top 1 count(ref_doc_no) cnt,ass_picker_id assignPicker \n" +
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
    @Query(value = "select count(ref_doc_no) pickerCount,ass_picker_id assignPicker \n" +
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
}