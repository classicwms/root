package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.report.HistoryReport;
import com.tekclover.wms.api.transaction.model.report.ITransactionHistoryReport;
import com.tekclover.wms.api.transaction.model.report.TransactionHistoryResults;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
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


    //Transaction History Report
    @Procedure
    void SP_THR(String companyCodeId,String plantId,String languageId,String warehouseId, String itemCode, String manufacturerName,
                Date openingStockDateFrom, Date openingStockDateTo, Date closingStockDateFrom, Date closingStockDateTo);


    @Query(value = " WITH THR AS ( \n" +
            "    SELECT\n" +
            "        I.C_ID,\n" +
            "        I.PLANT_ID,\n" +
            "        I.LANG_ID,\n" +
            "        I.WH_ID,\n" +
            "        I.ITM_CODE,\n" +
            "        I.MFR_PART AS MFR_NAME,\n" +
            "        I.TEXT,\n" +
            "        I.C_TEXT,\n" +
            "        I.PLANT_TEXT,\n" +
            "        I.WH_TEXT\n" +
            "    FROM TBLIMBASICDATA1 I\n" +
            "    WHERE I.C_ID = :companyCodeId\n" +
            "      AND I.PLANT_ID = :plantId\n" +
            "      AND I.LANG_ID = :languageId\n" +
            "      AND I.WH_ID = :warehouseId\n" +
            "      AND I.IS_DELETED = 0\n" +
            "      AND (:itemCode IS NULL OR I.ITM_CODE in (:itemCode))\n" +
            "      AND (:manufacturerName IS NULL OR I.MFR_PART = :manufacturerName)\n" +
            "),\n" +
            "\n" +
            "IVS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_NAME,\n" +
            "        SUM(INV_QTY + ALLOC_QTY) AS IS_OS_QTY\n" +
            "    FROM TBLINVENTORYSTOCK\n" +
            "    WHERE C_ID = :companyCodeId\n" +
            "      AND PLANT_ID = :plantId\n" +
            "      AND LANG_ID = :languageId\n" +
            "      AND WH_ID = :warehouseId\n" +
            "      AND BIN_CL_ID IN (1,7, 3, 2, 10)\n" +
            "      AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "IV AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_NAME,\n" +
            "        SUM(REF_FIELD_4) AS IV_QTY\n" +
            "    FROM TBLINVENTORY\n" +
            "    WHERE C_ID = :companyCodeId\n" +
            "      AND PLANT_ID = :plantId\n" +
            "      AND LANG_ID = :languageId\n" +
            "      AND WH_ID = :warehouseId\n" +
            "      AND BIN_CL_ID IN (1,2,5,10)\n" +
            "     AND inv_id in (select max(inv_id) from tblinventory group by itm_code,barcode_id,st_bin,plant_id,wh_id,c_id,lang_id) " +
            "      AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "PAL_OS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        SUM(PA_CNF_QTY) AS PA_OS_QTY\n" +
            "    FROM TBLPUTAWAYLINE\n" +
            "    WHERE C_ID = :companyCodeId\n" +
            "      AND PLANT_ID = :plantId\n" +
            "      AND LANG_ID = :languageId\n" +
            "      AND WH_ID = :warehouseId\n" +
            "      AND PA_CTD_ON BETWEEN :openingFrom AND :openingTo\n" +
            "      AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE\n" +
            "),\n" +
            "\n" +
            "PUL_OS AS (\n" +
            "    SELECT\n" +
            "        SKU_CODE,\n" +
            "        count(PIK_QTY) AS PI_OS_QTY\n" +
            "    FROM tbldeliveryconfirmation \n" +
            "    WHERE C_ID = :companyCodeId\n" +
            "      AND PLANT_ID = :plantId\n" +
            "      AND LANG_ID = :languageId\n" +
            "      AND WH_ID = :warehouseId\n" +
            "      AND ORDER_PROCESSED_ON BETWEEN :openingFrom AND :openingTo\n" +
            "    GROUP BY SKU_CODE\n" +
            "),\n" +
            "\n" +
            "IVM_OS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        SUM(MVT_QTY) AS IV_OS_QTY\n" +
            "    FROM TBLINVENTORYMOVEMENT\n" +
            "    WHERE C_ID = :companyCodeId\n" +
            "      AND PLANT_ID = :plantId\n" +
            "      AND LANG_ID = :languageId\n" +
            "      AND WH_ID = :warehouseId\n" +
            "      AND MVT_TYP_ID = 4\n" +
            "      AND SUB_MVT_TYP_ID = 1\n" +
            "      AND IM_CTD_ON BETWEEN :openingFrom AND :openingTo\n" +
            "      AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE\n" +
            "),\n" +
            "\n" +
            "PAL_CS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        SUM(PA_CNF_QTY) AS PA_CS_QTY\n" +
            "    FROM TBLPUTAWAYLINE\n" +
            "    WHERE IS_DELETED = 0 AND PA_CTD_ON BETWEEN :closingFrom AND :closingTo\n" +
            "    GROUP BY ITM_CODE\n" +
            "),\n" +
            "\n" +
            "PUL_CS AS (\n" +
            "    SELECT\n" +
            "        SKU_CODE,\n" +
            "        count(PIK_QTY) AS PI_CS_QTY\n" +
            "    FROM tbldeliveryconfirmation\n" +
            "    WHERE ORDER_PROCESSED_ON BETWEEN :closingFrom AND :closingTo\n" +
            "    GROUP BY SKU_CODE\n" +
            "),\n" +
            "\n" +
            "IVM_CS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        SUM(MVT_QTY) AS IV_CS_QTY\n" +
            "    FROM TBLINVENTORYMOVEMENT\n" +
            "    WHERE IS_DELETED = 0 AND IM_CTD_ON BETWEEN :closingFrom AND :closingTo\n" +
            "    GROUP BY ITM_CODE\n" +
            ")\n" +
            "\n" +
            "SELECT\n" +
            "    TH.C_ID            AS companyCodeId,\n" +
            "    TH.PLANT_ID        AS plantId,\n" +
            "    TH.LANG_ID         AS languageId,\n" +
            "    TH.WH_ID           AS warehouseId,\n" +
            "\n" +
            "    /* OPENING STOCK */\n" +
            "    (\n" +
            "        COALESCE(IVS.IS_OS_QTY,0)\n" +
            "      + COALESCE(PAL_OS.PA_OS_QTY,0)\n" +
            "      + COALESCE(IVM_OS.IV_OS_QTY,0)\n" +
            "      - COALESCE(PUL_OS.PI_OS_QTY,0)\n" +
            "    ) AS openingStock,\n" +
            "\n" +
            "    COALESCE(PAL_CS.PA_CS_QTY,0) AS inboundQty,\n" +
            "    COALESCE(PUL_CS.PI_CS_QTY,0) AS outboundQty,\n" +
            "    COALESCE(IVM_CS.IV_CS_QTY,0) AS stockAdjustmentQty,\n" +
            "    COALESCE(IV.IV_QTY,0)        AS systemInventory,\n" +
            "\n" +
            "    TH.ITM_CODE        AS itemCode,\n" +
            "    TH.TEXT            AS itemDescription,\n" +
            "    TH.C_TEXT as companyDescription,\n" +
            "    TH.PLANT_TEXT as plantDescription,\n" +
            "    TH.WH_TEXT as warehouseDescription,\n" +
            "    'WK' as manufacturerName,\n" +
            "\n" +
            "    /* CLOSING STOCK */\n" +
            "    (\n" +
            "        (\n" +
            "            COALESCE(IVS.IS_OS_QTY,0)\n" +
            "          + COALESCE(PAL_OS.PA_OS_QTY,0)\n" +
            "          + COALESCE(IVM_OS.IV_OS_QTY,0)\n" +
            "          - COALESCE(PUL_OS.PI_OS_QTY,0)\n" +
            "        )\n" +
            "        + COALESCE(PAL_CS.PA_CS_QTY,0)\n" +
            "        + COALESCE(IVM_CS.IV_CS_QTY,0)\n" +
            "        - COALESCE(PUL_CS.PI_CS_QTY,0)\n" +
            "    ) AS closingStock,\n" +
            "\n" +
            "    (\n" +
            "        (\n" +
            "            (\n" +
            "                COALESCE(IVS.IS_OS_QTY,0)\n" +
            "              + COALESCE(PAL_OS.PA_OS_QTY,0)\n" +
            "              + COALESCE(IVM_OS.IV_OS_QTY,0)\n" +
            "              - COALESCE(PUL_OS.PI_OS_QTY,0)\n" +
            "            )\n" +
            "            + COALESCE(PAL_CS.PA_CS_QTY,0)\n" +
            "            + COALESCE(IVM_CS.IV_CS_QTY,0)\n" +
            "            - COALESCE(PUL_CS.PI_CS_QTY,0)\n" +
            "        )\n" +
            "        - COALESCE(IV.IV_QTY,0)\n" +
            "    ) AS variance\n" +
            "FROM THR TH\n" +
            "LEFT JOIN IVS     ON TH.ITM_CODE = IVS.ITM_CODE \n" +
            "LEFT JOIN IV      ON TH.ITM_CODE = IV.ITM_CODE \n" +
            "LEFT JOIN PAL_OS  ON TH.ITM_CODE = PAL_OS.ITM_CODE \n" +
            "LEFT JOIN PUL_OS  ON TH.ITM_CODE = PUL_OS.SKU_CODE \n" +
            "LEFT JOIN IVM_OS  ON TH.ITM_CODE = IVM_OS.ITM_CODE \n" +
            "LEFT JOIN PAL_CS  ON TH.ITM_CODE = PAL_CS.ITM_CODE \n" +
            "LEFT JOIN PUL_CS  ON TH.ITM_CODE = PUL_CS.SKU_CODE \n" +
            "LEFT JOIN IVM_CS  ON TH.ITM_CODE = IVM_CS.ITM_CODE ", nativeQuery = true)
    List<HistoryReport> findTransactionHistoryReport(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("itemCode") List<String> itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("openingFrom") Date openingFrom,
            @Param("openingTo") Date openingTo,
            @Param("closingFrom") Date closingFrom,
            @Param("closingTo") Date closingTo);
}