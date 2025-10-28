package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.outbound.v2.PickListHeader;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional
public interface PickListHeaderRepository extends JpaRepository<PickListHeader, Long>,
        JpaSpecificationExecutor<PickListHeader>,
        StreamableJpaSpecificationRepository<PickListHeader> {

    @Transactional
    @Procedure(procedureName = "pick_list_cnf_update_proc")
    public void updatePickupLineQualityHeaderLineCnfByUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("oldRefDocNumber") String oldRefDocNumber,
            @Param("oldPreOutboundNo") String oldPreOutboundNo,
            @Param("newRefDocNumber") String newRefDocNumber,
            @Param("newPreOutboundNo") String newPreOutboundNo,
            @Param("salesOrderNo") String salesOrderNo);

    @Transactional
    @Procedure(procedureName = "pick_list_delete_proc")
    void updateDeletionIndicatorPickListCancellationProc(@Param("companyCodeId") String companyCodeId,
                                                         @Param("plantId") String plantId,
                                                         @Param("languageId") String languageId,
                                                         @Param("warehouseId") String warehouseId,
                                                         @Param("refDocNumber") String refDocNumber,
                                                         @Param("preOutboundNo") String preOutboundNo,
                                                         @Param("updatedBy") String updatedBy,
                                                         @Param("updatedOn") Date updatedOn);

    @Modifying
    @Transactional
    @Query(value =
            // tblpreoutboundline
            "UPDATE tblpreoutboundline SET IS_DELETED = 1, PRE_OB_UTD_BY = :updatedBy, PRE_OB_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tblpreoutboundheader
                    "UPDATE tblpreoutboundheader SET IS_DELETED = 1, PRE_OB_UTD_BY = :updatedBy, PRE_OB_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tblordermangementline
                    "UPDATE tblordermangementline SET IS_DELETED = 1, PICK_UP_UTD_BY = :updatedBy, PICK_UP_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tblordermangementheader
                    "UPDATE tblordermangementheader SET IS_DELETED = 1, PICK_UP_UTD_BY = :updatedBy, PICK_UP_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tbloutboundlinedup
                    "UPDATE tbloutboundlinedup SET IS_DELETED = 1, DLV_UTD_BY = :updatedBy, DLV_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tbloutboundline
                    "UPDATE tbloutboundline SET IS_DELETED = 1, DLV_UTD_BY = :updatedBy, DLV_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tbloutboundheader
                    "UPDATE tbloutboundheader SET IS_DELETED = 1, DLV_UTD_BY = :updatedBy, DLV_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tblpickupline
                    "UPDATE tblpickupline SET IS_DELETED = 1, PICK_UTD_BY = :updatedBy, PICK_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tblpickupheader
                    "UPDATE tblpickupheader SET IS_DELETED = 1, PICK_UTD_BY = :updatedBy, PICK_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tblqualityline
                    "UPDATE tblqualityline SET IS_DELETED = 1, QC_UTD_BY = :updatedBy, QC_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tblqualityheader
                    "UPDATE tblqualityheader SET IS_DELETED = 1, QC_UTD_BY = :updatedBy, QC_UTD_ON = :updatedOn " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = 0; " +

                    // tblinventorymovement
                    "UPDATE tblinventorymovement SET IS_DELETED = 1 " +
                    "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
                    "AND REF_DOC_NO = :refDocNumber AND REF_NO = :preOutboundNo AND IS_DELETED = 0; ",
            nativeQuery = true)
    void deletePickListRecords(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

}