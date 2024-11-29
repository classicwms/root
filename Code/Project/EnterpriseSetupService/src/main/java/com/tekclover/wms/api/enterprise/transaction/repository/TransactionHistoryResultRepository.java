package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.report.ITransactionHistoryReport;
import com.tekclover.wms.api.enterprise.transaction.model.report.TransactionHistoryResults;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface TransactionHistoryResultRepository extends JpaRepository<TransactionHistoryResults, Long>,
        JpaSpecificationExecutor<TransactionHistoryResults>,
        StreamableJpaSpecificationRepository<TransactionHistoryResults> {


//    //-------------------------------create table and update zero for all computable fields-----------------------------------------//
//    //Truncate Table
//    @Query(value = "truncate table tbltransactionhistoryresults  \n"
//            + "select count(id) from tbltransactionhistoryresults ", nativeQuery = true)
//    public long truncateTblTransactionHistoryResults();
//
//    //create table and update the table with itemCode and itemDescription
//    @Query(value = "insert into tbltransactionhistoryresults(item_code,description,warehouse_id) \n"
//            + "select itm_code,text description,wh_id warehouseId from tblimbasicdata1 where wh_id = :warehouseId and is_deleted=0 and \n"
//            + "(COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))) \n"
//            + "select max(id) from tbltransactionhistoryresults ", nativeQuery = true)
//    public Long createTblTransactionHistoryResults(@Param(value = "itemCode") List<String> itemCode,
//                                                   @Param(value = "warehouseId") String warehouseId);
//
//
//    //--------------------------------------------------------------Opening Stock-------------------------------------------------------------//
//
//    //Inventory Stock Table
//    @Query(value = "UPDATE th SET th.is_os_qty = x.value FROM tbltransactionhistoryresults th INNER JOIN \n" +
//            " (SELECT (SUM(COALESCE(INV_QTY,0)) + SUM(COALESCE(ALLOC_QTY,0))) value,ITM_CODE itemCode FROM tblinventorystock \n" +
//            " WHERE ITM_CODE IN \n" +
//            " ((SELECT ITM_CODE FROM TBLPUTAWAYLINE WHERE WH_ID=:warehouse AND STATUS_ID IN (20,22) AND IS_DELETED=0 AND \n" +
//            " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n" +
//            " union \n" +
//            " (SELECT ITM_CODE FROM TBLPICKUPLINE WHERE WH_ID=:warehouse AND STATUS_ID IN (50,59) AND IS_DELETED=0 AND \n" +
//            " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n" +
//            " union \n" +
//            " (SELECT ITM_CODE FROM TBLINVENTORYMOVEMENT WHERE WH_ID=:warehouse AND MVT_TYP_ID =4 AND SUB_MVT_TYP_ID=1 AND IS_DELETED=0 AND \n" +
//            " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n" +
//            " AND WH_ID=:warehouse AND BIN_CL_ID in (1,4) GROUP BY ITM_CODE) X ON X.ITEMCODE=TH.ITEM_CODE \n" +
//            " select count(is_os_qty) from tbltransactionhistoryresults where is_os_qty != 0", nativeQuery = true)
//    public Long findSumOfInventoryQtyAndAllocQtyList(@Param(value = "itemCode") List<String> itemCode,
//                                                     @Param(value = "warehouse") String warehouse);
//
//    //PutAway
//    @Query(value = "update th set th.pa_os_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
//            + "(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblputawayline WHERE ITM_CODE IN \n"
//            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
//            + "AND STATUS_ID = 20 AND WH_ID=:warehouse AND IS_DELETED = 0 AND PA_CTD_ON BETWEEN :openingStockDateFrom and :openingStockDateTo group by itm_code)x on x.itemCode=th.item_code \n"
//            + "select count(pa_os_qty) from tbltransactionhistoryresults where pa_os_qty != 0", nativeQuery = true)
//    public Long findSumOfPAConfirmQty_New(@Param(value = "itemCode") List<String> itemCode,
//                                          @Param(value = "openingStockDateFrom") Date openingStockDateFrom,
//                                          @Param(value = "openingStockDateTo") Date openingStockDateTo,
//                                          @Param(value = "warehouse") String warehouse);
//
//    //PutAwayReversal
//    @Query(value = "update th set th.pa_os_re_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
//            + "(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblputawayline WHERE ITM_CODE IN \n"
//            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
//            + "AND STATUS_ID = 22 AND WH_ID=:warehouse AND IS_DELETED = 0 AND PA_CTD_ON BETWEEN :openingStockDateFrom and :openingStockDateTo group by itm_code)x on x.itemCode=th.item_code \n"
//            + "select count(pa_os_re_qty) from tbltransactionhistoryresults where pa_os_re_qty != 0", nativeQuery = true)
//    public Long findSumOfPAConfirmQty_NewReversal(@Param(value = "itemCode") List<String> itemCode,
//                                                  @Param(value = "openingStockDateFrom") Date openingStockDateFrom,
//                                                  @Param(value = "openingStockDateTo") Date openingStockDateTo,
//                                                  @Param(value = "warehouse") String warehouse);
//
//    //PickupLine
//    @Query(value = "update th set th.pi_os_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
//            + "(SELECT SUM(PICK_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblpickupline WHERE ITM_CODE IN \n"
//            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
//            + "AND STATUS_ID in (50,59) AND WH_ID=:warehouse AND IS_DELETED = 0 AND PICK_CTD_ON BETWEEN :openingStockDateFrom and :openingStockDateTo group by ITM_CODE)x on x.itemCode=th.item_code \n"
//            + "select count(pi_os_qty) from tbltransactionhistoryresults where pi_os_qty != 0", nativeQuery = true)
//    public Long findSumOfPickupLineQtyNew(@Param(value = "itemCode") List<String> itemCode,
//                                          @Param(value = "openingStockDateFrom") Date openingStockDateFrom,
//                                          @Param(value = "openingStockDateTo") Date openingStockDateTo,
//                                          @Param(value = "warehouse") String warehouse);
//
//    //inventoryMovement
//    @Query(value = "update th set th.iv_os_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
//            + "(SELECT SUM(MVT_QTY) VALUE,ITM_CODE itemCode FROM tblinventorymovement WHERE ITM_CODE IN \n"
//            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
//            + "AND MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 AND WH_ID=:warehouse AND IS_DELETED = 0 AND IM_CTD_ON BETWEEN :openingStockDateFrom and :openingStockDateTo group by itm_code)x on x.itemCode=th.item_code\n"
//            + "select count(iv_os_qty) from tbltransactionhistoryresults where iv_os_qty != 0", nativeQuery = true)
//    public Long findSumOfMvtQtyNew(@Param(value = "itemCode") List<String> itemCode,
//                                   @Param(value = "openingStockDateFrom") Date openingStockDateFrom,
//                                   @Param(value = "openingStockDateTo") Date openingStockDateTo,
//                                   @Param(value = "warehouse") String warehouse);
//
//    //--------------------------------------------------------------Closing Stock-------------------------------------------------------------//
//
//    //PutAway
//    @Query(value = "update th set th.pa_cs_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
//            + "(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblputawayline WHERE ITM_CODE IN \n"
//            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
//            + "AND STATUS_ID = 20 AND WH_ID=:warehouse AND IS_DELETED = 0 AND PA_CTD_ON BETWEEN :closingStockDateFrom and :closingStockDateTo group by itm_code)x on x.itemCode=th.item_code \n"
//            + "select count(pa_cs_qty) from tbltransactionhistoryresults where pa_cs_qty != 0", nativeQuery = true)
//    public Long findSumOfPAConfirmQtyClosingStock(@Param(value = "itemCode") List<String> itemCode,
//                                                  @Param(value = "closingStockDateFrom") Date closingStockDateFrom,
//                                                  @Param(value = "closingStockDateTo") Date closingStockDateTo,
//                                                  @Param(value = "warehouse") String warehouse);
//
//    //PutAwayReversal
//    @Query(value = "update th set th.pa_cs_re_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
//            + "(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblputawayline WHERE ITM_CODE IN \n"
//            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
//            + "AND STATUS_ID = 22 AND WH_ID=:warehouse AND IS_DELETED = 0 AND PA_CTD_ON BETWEEN :closingStockDateFrom and :closingStockDateTo group by itm_code)x on x.itemCode=th.item_code \n"
//            + "select count(pa_cs_re_qty) from tbltransactionhistoryresults where pa_cs_re_qty != 0", nativeQuery = true)
//    public Long findSumOfPAConfirmQtyClosingStockReversal(@Param(value = "itemCode") List<String> itemCode,
//                                                          @Param(value = "closingStockDateFrom") Date closingStockDateFrom,
//                                                          @Param(value = "closingStockDateTo") Date closingStockDateTo,
//                                                          @Param(value = "warehouse") String warehouse);
//
//    //PickupLine
//    @Query(value = "update th set th.pi_cs_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
//            + "(SELECT SUM(PICK_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblpickupline WHERE ITM_CODE IN \n"
//            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
//            + "AND STATUS_ID in (50,59) AND WH_ID=:warehouse AND IS_DELETED = 0 AND PICK_CTD_ON BETWEEN :closingStockDateFrom and :closingStockDateTo group by ITM_CODE)x on x.itemCode=th.item_code \n"
//            + "select count(pi_cs_qty) from tbltransactionhistoryresults where pi_cs_qty != 0", nativeQuery = true)
//    public Long findSumOfPickupLineQtyClosingStock(@Param(value = "itemCode") List<String> itemCode,
//                                                   @Param(value = "closingStockDateFrom") Date closingStockDateFrom,
//                                                   @Param(value = "closingStockDateTo") Date closingStockDateTo,
//                                                   @Param(value = "warehouse") String warehouse);
//
//    //InventoryMovement
//    @Query(value = "update th set th.iv_cs_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
//            + "(SELECT SUM(MVT_QTY) VALUE,ITM_CODE itemCode FROM tblinventorymovement WHERE ITM_CODE IN \n"
//            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
//            + "union\n"
//            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
//            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
//            + "AND MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 AND WH_ID=:warehouse AND IS_DELETED = 0 AND IM_CTD_ON BETWEEN :closingStockDateFrom and :closingStockDateTo group by itm_code)x on x.itemCode=th.item_code \n"
//            + "select count(iv_cs_qty) from tbltransactionhistoryresults where iv_cs_qty != 0", nativeQuery = true)
//    public Long findSumOfMvtQtyClosingStock(@Param(value = "itemCode") List<String> itemCode,
//                                            @Param(value = "closingStockDateFrom") Date closingStockDateFrom,
//                                            @Param(value = "closingStockDateTo") Date closingStockDateTo,
//                                            @Param(value = "warehouse") String warehouse);
//
//    //finalResult
//    @Query(value = "select *, \n" +
//            " (openingStock+inboundQty+stockAdjustmentQty-outboundQty) closingStock \n" +
//            " from \n" +
//            " (select \n" +
////			" ((COALESCE(is_os_qty,0)+(COALESCE(pa_os_qty,0)-COALESCE(pa_os_re_qty,0))+COALESCE(iv_os_qty,0))-COALESCE(pi_os_qty,0)) openingStock, \n"+
//            " ((COALESCE(is_os_qty,0)+COALESCE(pa_os_qty,0)+COALESCE(iv_os_qty,0))-COALESCE(pi_os_qty,0)) openingStock, \n" +
////			" (COALESCE(pa_cs_qty,0)-COALESCE(pa_cs_re_qty,0)) inboundQty, \n"+
//            " COALESCE(pa_cs_qty,0) inboundQty, \n" +
//            " COALESCE(pi_cs_qty,0) outboundQty, \n" +
//            " COALESCE(iv_cs_qty,0) stockAdjustmentQty, \n" +
//            " item_code itemCode, \n" +
//            " warehouse_id warehouseId, \n" +
//            " description itemDescription \n" +
//            " from tbltransactionhistoryresults) x ", nativeQuery = true)
//    public List<ITransactionHistoryReport> findTransactionHistoryReport();

    //-------------------------------create table and update zero for all computable fields-----------------------------------------//
    //Truncate Table
    @Modifying
    @Transactional
    @Query(value = "truncate table tbltransactionhistoryresults", nativeQuery = true)
    public void truncateTblTransactionHistoryResults();

    //create table and update the table with itemCode and itemDescription
    @Modifying
    @Transactional
    @Query(value = "insert into tbltransactionhistoryresults(item_code,description,warehouse_id) \n"
            + "select itm_code,text description,wh_id warehouseId from tblimbasicdata1 where wh_id = :warehouseId and is_deleted=0 and \n"
            + "(COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))) ", nativeQuery = true)
    public void createTblTransactionHistoryResults(@Param(value = "itemCode") List<String> itemCode,
                                                   @Param(value = "warehouseId") String warehouseId);


    //--------------------------------------------------------------Opening Stock-------------------------------------------------------------//

    //Inventory Stock Table
    @Modifying
    @Transactional
    @Query(value = "UPDATE th SET th.is_os_qty = x.value FROM tbltransactionhistoryresults th INNER JOIN \n" +
            " (SELECT (SUM(COALESCE(INV_QTY,0)) + SUM(COALESCE(ALLOC_QTY,0))) value,ITM_CODE itemCode FROM tblinventorystock \n" +
            " WHERE ITM_CODE IN \n" +
            " ((SELECT ITM_CODE FROM TBLPUTAWAYLINE WHERE WH_ID=:warehouse AND STATUS_ID IN (20,22) AND IS_DELETED=0 AND \n" +
            " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n" +
            " union \n" +
            " (SELECT ITM_CODE FROM TBLPICKUPLINE WHERE WH_ID=:warehouse AND STATUS_ID IN (50,59) AND IS_DELETED=0 AND \n" +
            " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n" +
            " union \n" +
            " (SELECT ITM_CODE FROM TBLINVENTORYMOVEMENT WHERE WH_ID=:warehouse AND MVT_TYP_ID =4 AND SUB_MVT_TYP_ID=1 AND IS_DELETED=0 AND \n" +
            " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n" +
            " AND WH_ID=:warehouse AND BIN_CL_ID in (1,4) GROUP BY ITM_CODE) X ON X.ITEMCODE=TH.ITEM_CODE ", nativeQuery = true)
    public void findSumOfInventoryQtyAndAllocQtyList(@Param(value = "itemCode") List<String> itemCode,
                                                     @Param(value = "warehouse") String warehouse);

    //PutAway
    @Modifying
    @Transactional
    @Query(value = "update th set th.pa_os_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
            + "(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblputawayline WHERE ITM_CODE IN \n"
            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
            + "AND STATUS_ID = 20 AND WH_ID=:warehouse AND IS_DELETED = 0 AND PA_CTD_ON BETWEEN :openingStockDateFrom and :openingStockDateTo group by itm_code)x on x.itemCode=th.item_code ", nativeQuery = true)
    public void findSumOfPAConfirmQty_New(@Param(value = "itemCode") List<String> itemCode,
                                          @Param(value = "openingStockDateFrom") Date openingStockDateFrom,
                                          @Param(value = "openingStockDateTo") Date openingStockDateTo,
                                          @Param(value = "warehouse") String warehouse);

    //PutAwayReversal
    @Modifying
    @Transactional
    @Query(value = "update th set th.pa_os_re_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
            + "(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblputawayline WHERE ITM_CODE IN \n"
            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
            + "AND STATUS_ID = 22 AND WH_ID=:warehouse AND IS_DELETED = 0 AND PA_CTD_ON BETWEEN :openingStockDateFrom and :openingStockDateTo group by itm_code)x on x.itemCode=th.item_code ", nativeQuery = true)
    public void findSumOfPAConfirmQty_NewReversal(@Param(value = "itemCode") List<String> itemCode,
                                                  @Param(value = "openingStockDateFrom") Date openingStockDateFrom,
                                                  @Param(value = "openingStockDateTo") Date openingStockDateTo,
                                                  @Param(value = "warehouse") String warehouse);

    //PickupLine
    @Modifying
    @Transactional
    @Query(value = "update th set th.pi_os_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
            + "(SELECT SUM(PICK_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblpickupline WHERE ITM_CODE IN \n"
            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
            + "AND STATUS_ID in (50,59) AND WH_ID=:warehouse AND IS_DELETED = 0 AND PICK_CTD_ON BETWEEN :openingStockDateFrom and :openingStockDateTo group by ITM_CODE)x on x.itemCode=th.item_code ", nativeQuery = true)
    public void findSumOfPickupLineQtyNew(@Param(value = "itemCode") List<String> itemCode,
                                          @Param(value = "openingStockDateFrom") Date openingStockDateFrom,
                                          @Param(value = "openingStockDateTo") Date openingStockDateTo,
                                          @Param(value = "warehouse") String warehouse);

    //inventoryMovement
    @Modifying
    @Transactional
    @Query(value = "update th set th.iv_os_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
            + "(SELECT SUM(MVT_QTY) VALUE,ITM_CODE itemCode FROM tblinventorymovement WHERE ITM_CODE IN \n"
            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
            + "AND MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 AND WH_ID=:warehouse AND IS_DELETED = 0 AND IM_CTD_ON BETWEEN :openingStockDateFrom and :openingStockDateTo group by itm_code)x on x.itemCode=th.item_code", nativeQuery = true)
    public void findSumOfMvtQtyNew(@Param(value = "itemCode") List<String> itemCode,
                                   @Param(value = "openingStockDateFrom") Date openingStockDateFrom,
                                   @Param(value = "openingStockDateTo") Date openingStockDateTo,
                                   @Param(value = "warehouse") String warehouse);

    //--------------------------------------------------------------Closing Stock-------------------------------------------------------------//

    //PutAway
    @Modifying
    @Transactional
    @Query(value = "update th set th.pa_cs_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
            + "(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblputawayline WHERE ITM_CODE IN \n"
            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
            + "AND STATUS_ID = 20 AND WH_ID=:warehouse AND IS_DELETED = 0 AND PA_CTD_ON BETWEEN :closingStockDateFrom and :closingStockDateTo group by itm_code)x on x.itemCode=th.item_code ", nativeQuery = true)
    public void findSumOfPAConfirmQtyClosingStock(@Param(value = "itemCode") List<String> itemCode,
                                                  @Param(value = "closingStockDateFrom") Date closingStockDateFrom,
                                                  @Param(value = "closingStockDateTo") Date closingStockDateTo,
                                                  @Param(value = "warehouse") String warehouse);

    //PutAwayReversal
    @Modifying
    @Transactional
    @Query(value = "update th set th.pa_cs_re_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
            + "(SELECT SUM(PA_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblputawayline WHERE ITM_CODE IN \n"
            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
            + "AND STATUS_ID = 22 AND WH_ID=:warehouse AND IS_DELETED = 0 AND PA_CTD_ON BETWEEN :closingStockDateFrom and :closingStockDateTo group by itm_code)x on x.itemCode=th.item_code ", nativeQuery = true)
    public void findSumOfPAConfirmQtyClosingStockReversal(@Param(value = "itemCode") List<String> itemCode,
                                                          @Param(value = "closingStockDateFrom") Date closingStockDateFrom,
                                                          @Param(value = "closingStockDateTo") Date closingStockDateTo,
                                                          @Param(value = "warehouse") String warehouse);

    //PickupLine
    @Modifying
    @Transactional
    @Query(value = "update th set th.pi_cs_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
            + "(SELECT SUM(PICK_CNF_QTY) VALUE,ITM_CODE itemCode FROM tblpickupline WHERE ITM_CODE IN \n"
            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
            + "AND STATUS_ID in (50,59) AND WH_ID=:warehouse AND IS_DELETED = 0 AND PICK_CTD_ON BETWEEN :closingStockDateFrom and :closingStockDateTo group by ITM_CODE)x on x.itemCode=th.item_code ", nativeQuery = true)
    public void findSumOfPickupLineQtyClosingStock(@Param(value = "itemCode") List<String> itemCode,
                                                   @Param(value = "closingStockDateFrom") Date closingStockDateFrom,
                                                   @Param(value = "closingStockDateTo") Date closingStockDateTo,
                                                   @Param(value = "warehouse") String warehouse);

    //InventoryMovement
    @Modifying
    @Transactional
    @Query(value = "update th set th.iv_cs_qty = x.VALUE from tbltransactionhistoryresults th inner join \n"
            + "(SELECT SUM(MVT_QTY) VALUE,ITM_CODE itemCode FROM tblinventorymovement WHERE ITM_CODE IN \n"
            + "((select itm_code from tblputawayline where wh_id=:warehouse and status_id in (20,22) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblpickupline where wh_id=:warehouse and status_id in (50,59) and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode)))) \n"
            + "union\n"
            + "(select itm_code from tblinventorymovement where wh_id=:warehouse and MVT_TYP_ID =4 and SUB_MVT_TYP_ID=1 and is_deleted=0 and \n"
            + " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))))) \n"
            + "AND MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 AND WH_ID=:warehouse AND IS_DELETED = 0 AND IM_CTD_ON BETWEEN :closingStockDateFrom and :closingStockDateTo group by itm_code)x on x.itemCode=th.item_code ", nativeQuery = true)
    public void findSumOfMvtQtyClosingStock(@Param(value = "itemCode") List<String> itemCode,
                                            @Param(value = "closingStockDateFrom") Date closingStockDateFrom,
                                            @Param(value = "closingStockDateTo") Date closingStockDateTo,
                                            @Param(value = "warehouse") String warehouse);

    //finalResult
    @Query(value = "select *, \n" +
            " (openingStock+inboundQty+stockAdjustmentQty-outboundQty) closingStock \n" +
            " from \n" +
            " (select \n" +
            " ((COALESCE(is_os_qty,0)+COALESCE(pa_os_qty,0)+COALESCE(iv_os_qty,0))-COALESCE(pi_os_qty,0)) openingStock, \n" +
            " COALESCE(pa_cs_qty,0) inboundQty, \n" +
            " COALESCE(pi_cs_qty,0) outboundQty, \n" +
            " COALESCE(iv_cs_qty,0) stockAdjustmentQty, \n" +
            " item_code itemCode, \n" +
            " warehouse_id warehouseId, \n" +
            " description itemDescription \n" +
            " from tbltransactionhistoryresults) x ", nativeQuery = true)
    public List<ITransactionHistoryReport> findTransactionHistoryReport();

    //-------------------------------stored Procedures-----------------------------------------//

    //create table and update the table with itemCode and itemDescription
    @Procedure
    void SP_INSERT_THR(String warehouseId, String itemCode);

    //--------------------------------------------------------------Opening Stock-------------------------------------------------------------//

    //Inventory Stock Table

    @Procedure
    void SP_UPDATE_THR_INV_OS(String warehouse, String itemCode);

    //PutAway
    @Procedure
    void SP_UPDATE_THR_PUTAWAY_OS(String warehouse, String itemCode, Date openingStockDateFrom, Date openingStockDateTo);

    //PickupLine
    @Procedure
    void SP_UPDATE_THR_PICKUP_OS(String warehouse, String itemCode, Date openingStockDateFrom, Date openingStockDateTo);

    //inventoryMovement
    @Procedure
    void SP_UPDATE_THR_INV_MMT_OS(String warehouse, String itemCode, Date openingStockDateFrom, Date openingStockDateTo);

    //--------------------------------------------------------------Closing Stock-------------------------------------------------------------//

    //PutAway
    @Procedure
    void SP_UPDATE_THR_PUTAWAY_CS(String warehouse, String itemCode, Date closingStockDateFrom, Date closingStockDateTo);

    //PickupLine
    @Procedure
    void SP_UPDATE_THR_PICKUP_CS(String warehouse, String itemCode, Date closingStockDateFrom, Date closingStockDateTo);

    //InventoryMovement
    @Procedure
    void SP_UPDATE_THR_INV_MMT_CS(String warehouse, String itemCode, Date closingStockDateFrom, Date closingStockDateTo);

    //Transaction History Report
    @Procedure
    void SP_THR(String warehouseId, String itemCode, Date openingStockDateFrom, Date openingStockDateTo, Date closingStockDateFrom, Date closingStockDateTo);

        @Query(value = "select  \n" +
            " closing_stock closingStock \n" +
            " opening_stock openingStock, \n" +
            " inbound_qty inboundQty, \n" +
            " outbound_qty outboundQty, \n" +
            " stock_adjustment_qty stockAdjustmentQty, \n" +
            " item_code itemCode, \n" +
            " system_inventory systemInventory, \n" +
            " warehouse_id warehouseId, \n" +
            " description itemDescription \n" +
            " from tbltransactionhistoryresults ", nativeQuery = true)
    public List<ITransactionHistoryReport> findNewTransactionHistoryReport();

}