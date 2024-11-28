package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.transaction.model.report.TransactionDetailDashBoardImpl;
import com.tekclover.wms.api.transaction.model.report.TransactionDetailsDashBoard;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface TransactionDetailsDashBoardRepository extends JpaRepository<TransactionDetailsDashBoard, Long>,
        JpaSpecificationExecutor<TransactionDetailsDashBoard>,
        StreamableJpaSpecificationRepository<TransactionDetailsDashBoard> {

    TransactionDetailsDashBoard findByTransactionId(Long transactionId);

//    @Query(value = "select itm_code itemCode,bar_id barcodeId, process, ctd_on createdOn, sum(qty) quantity \n" +
//            " from tbltransactiondetailsdashboard \n" +
//            " WHERE isRead :readStatus and \n" +
//            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
//            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
//            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
//            "(COALESCE(:manufacturerName, null) IS NULL OR (MFR_NAME IN (:manufacturerName))) and \n" +
//            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
//            "(COALESCE(:process, null) IS NULL OR (PROCESS IN (:process))) and\n" +
//            "(COALESCE(:barcodeId, null) IS NULL OR (BAR_ID IN (:barcodeId))) and \n" +
//            "(COALESCE(:createdBy, null) IS NULL OR (CTD_BY IN (:createdBy))) and\n" +
//            "(COALESCE(:updatedBy, null) IS NULL OR (UTD_BY IN (:updatedBy))) and\n" +
//            "(COALESCE(CONVERT(VARCHAR(255), :startCreatedOn), null) IS NULL OR (CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startCreatedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endCreatedOn), null))) \n" +
//            "GROUP BY itm_code,bard_id,", nativeQuery = true)
//    public List<TransactionDetailDashBoardImpl> findTransactionDetail(@Param("companyCodeId") List<String> companyCodeId,
//                                                                      @Param("plantId") List<String> plantId,
//                                                                      @Param("languageId") List<String> languageId,
//                                                                      @Param("warehouseId") List<String> warehouseId,
//                                                                      @Param("itemCode") List<String> itemCode,
//                                                                      @Param("process") List<String> process,
//                                                                      @Param("barcodeId") List<String> barcodeId,
//                                                                      @Param("createdBy") List<String> createdBy,
//                                                                      @Param("updatedBy") List<String> updatedBy,
//                                                                      @Param("transactionId") List<Long> transactionId,
//                                                                      @Param("startCreatedOn") Date startCreatedOn,
//                                                                      @Param("endCreatedOn") Date endCreatedOn);
}