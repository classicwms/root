package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.report.HistoryReport;
import com.tekclover.wms.api.outbound.transaction.model.report.TransactionHistoryResults;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import com.tekclover.wms.api.outbound.transaction.model.report.ITransactionHistoryReport;
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
            "      AND BIN_CL_ID IN (1,7)\n" +
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
            "      AND BIN_CL_ID IN (1,2,5,7)\n" +
            "     AND inv_id in (select max(inv_id) from tblinventory group by itm_code,pal_code,barcode_id,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) " +
            "      AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "PAL_OS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_NAME,\n" +
            "        SUM(PA_CNF_QTY) AS PA_OS_QTY\n" +
            "    FROM TBLPUTAWAYLINE\n" +
            "    WHERE C_ID = :companyCodeId\n" +
            "      AND PLANT_ID = :plantId\n" +
            "      AND LANG_ID = :languageId\n" +
            "      AND WH_ID = :warehouseId\n" +
            "      AND STATUS_ID IN (20,24)\n" +
            "      AND PA_CTD_ON BETWEEN :openingFrom AND :openingTo\n" +
            "      AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "PUL_OS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_NAME,\n" +
            "        SUM(PICK_CNF_QTY) AS PI_OS_QTY\n" +
            "    FROM TBLPICKUPLINE\n" +
            "    WHERE C_ID = :companyCodeId\n" +
            "      AND PLANT_ID = :plantId\n" +
            "      AND LANG_ID = :languageId\n" +
            "      AND WH_ID = :warehouseId\n" +
            "      AND STATUS_ID IN (50,57,59)\n" +
            "      AND PICK_CTD_ON BETWEEN :openingFrom AND :openingTo\n" +
            "      AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "IVM_OS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_PART,\n" +
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
            "    GROUP BY ITM_CODE, MFR_PART\n" +
            "),\n" +
            "\n" +
            "PAL_CS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_NAME,\n" +
            "        SUM(PA_CNF_QTY) AS PA_CS_QTY\n" +
            "    FROM TBLPUTAWAYLINE\n" +
            "    WHERE PA_CTD_ON BETWEEN :closingFrom AND :closingTo\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "PUL_CS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_NAME,\n" +
            "        SUM(PICK_CNF_QTY) AS PI_CS_QTY\n" +
            "    FROM TBLPICKUPLINE\n" +
            "    WHERE PICK_CTD_ON BETWEEN :closingFrom AND :closingTo\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "IVM_CS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_PART,\n" +
            "        SUM(MVT_QTY) AS IV_CS_QTY\n" +
            "    FROM TBLINVENTORYMOVEMENT\n" +
            "    WHERE IM_CTD_ON BETWEEN :closingFrom AND :closingTo\n" +
            "    GROUP BY ITM_CODE, MFR_PART\n" +
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
            "    TH.MFR_NAME as manufacturerName,\n" +
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
            "LEFT JOIN IVS     ON TH.ITM_CODE = IVS.ITM_CODE AND TH.MFR_NAME = IVS.MFR_NAME\n" +
            "LEFT JOIN IV      ON TH.ITM_CODE = IV.ITM_CODE AND TH.MFR_NAME = IV.MFR_NAME\n" +
            "LEFT JOIN PAL_OS  ON TH.ITM_CODE = PAL_OS.ITM_CODE AND TH.MFR_NAME = PAL_OS.MFR_NAME\n" +
            "LEFT JOIN PUL_OS  ON TH.ITM_CODE = PUL_OS.ITM_CODE AND TH.MFR_NAME = PUL_OS.MFR_NAME\n" +
            "LEFT JOIN IVM_OS  ON TH.ITM_CODE = IVM_OS.ITM_CODE AND TH.MFR_NAME = IVM_OS.MFR_PART\n" +
            "LEFT JOIN PAL_CS  ON TH.ITM_CODE = PAL_CS.ITM_CODE AND TH.MFR_NAME = PAL_CS.MFR_NAME\n" +
            "LEFT JOIN PUL_CS  ON TH.ITM_CODE = PUL_CS.ITM_CODE AND TH.MFR_NAME = PUL_CS.MFR_NAME\n" +
            "LEFT JOIN IVM_CS  ON TH.ITM_CODE = IVM_CS.ITM_CODE AND TH.MFR_NAME = IVM_CS.MFR_PART ", nativeQuery = true)
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

    @Query(value =
            "WITH THR AS ( " +
                    "    SELECT " +
                    "        I.C_ID, " +
                    "        I.PLANT_ID, " +
                    "        I.LANG_ID, " +
                    "        I.WH_ID, " +
                    "        UPPER(LTRIM(RTRIM(I.ITM_CODE))) AS ITM_CODE, " +
                    "        I.TEXT, " +
                    "        I.C_TEXT, " +
                    "        I.PLANT_TEXT, " +
                    "        I.WH_TEXT " +
                    "    FROM TBLIMBASICDATA1 I " +
                    "    WHERE I.C_ID = :companyCodeId " +
                    "      AND I.PLANT_ID = :plantId " +
                    "      AND I.LANG_ID = :languageId " +
                    "      AND I.WH_ID = :warehouseId " +
                    "      AND I.IS_DELETED = 0 " +
                    "      AND (:itemCode IS NULL OR I.ITM_CODE IN (:itemCode)) " +
                    "), " +

                    "IVS AS ( " +
                    "    SELECT " +
                    "        UPPER(LTRIM(RTRIM(ITM_CODE))) AS ITM_CODE, " +
                    "        SUM(INV_QTY + ALLOC_QTY) AS IS_OS_QTY " +
                    "    FROM TBLINVENTORYSTOCKBFS " +
                    "    WHERE C_ID = :companyCodeId " +
                    "      AND PLANT_ID = :plantId " +
                    "      AND LANG_ID = :languageId " +
                    "      AND WH_ID = :warehouseId " +
                    "      AND BIN_CL_ID IN (1,7) " +
                    "      AND IS_DELETED = 0 " +
                    "      AND (:itemCode IS NULL OR ITM_CODE IN (:itemCode)) " +
                    "    GROUP BY UPPER(LTRIM(RTRIM(ITM_CODE))) " +
                    "), " +

                    " IV AS ( " +
                    " SELECT " +
                    "  UPPER(LTRIM(RTRIM(itm_code))) AS ITM_CODE, " +
                    "SUM(REF_FIELD_4) AS IV_QTY " +
                    " FROM TBLINVENTORY " +
                    " WHERE C_ID = :companyCodeId " +
                    " AND PLANT_ID = :plantId " +
                    "AND LANG_ID = :languageId " +
                    "AND WH_ID = :warehouseId " +
                    "AND IS_DELETED = 0 " +
                    "AND (:itemCode IS NULL OR ITM_CODE IN (:itemCode)) " +
                    "AND inv_id IN ( " +
                    " SELECT MAX(inv_id) " +
                    " FROM tblinventory " +
                    "WHERE is_deleted = 0 GROUP BY itm_code, barcode_id, mfr_name, pack_barcode, alt_uom, bag_size, st_bin, plant_id, wh_id, c_id, lang_id) " +
                    "GROUP BY UPPER(LTRIM(RTRIM(itm_code))) " +
                    "), " +
                    "PAL_OS AS ( " +
                    "    SELECT " +
                    "        UPPER(LTRIM(RTRIM(ITM_CODE))) AS ITM_CODE, " +
                    "        SUM(PA_CNF_QTY) AS PA_OS_QTY " +
                    "    FROM TBLPUTAWAYLINE " +
                    "    WHERE C_ID = :companyCodeId " +
                    "      AND PLANT_ID = :plantId " +
                    "      AND LANG_ID = :languageId " +
                    "      AND WH_ID = :warehouseId " +
                    "      AND STATUS_ID IN (20,24) " +
                    "      AND PA_CTD_ON BETWEEN :openingFrom AND :openingTo " +
                    "      AND IS_DELETED = 0 " +
                    "      AND (:itemCode IS NULL OR ITM_CODE IN (:itemCode)) " +
                    "    GROUP BY UPPER(LTRIM(RTRIM(ITM_CODE))) " +
                    "), " +

                    "PUL_OS AS ( " +
                    "    SELECT " +
                    "        UPPER(LTRIM(RTRIM(ITM_CODE))) AS ITM_CODE, " +
                    "        SUM(PICK_CNF_QTY) AS PI_OS_QTY " +
                    "    FROM TBLPICKUPLINE " +
                    "    WHERE C_ID = :companyCodeId " +
                    "      AND PLANT_ID = :plantId " +
                    "      AND LANG_ID = :languageId " +
                    "      AND WH_ID = :warehouseId " +
                    "      AND STATUS_ID IN (50,57,59) " +
                    "      AND PICK_CTD_ON BETWEEN :openingFrom AND :openingTo " +
                    "      AND IS_DELETED = 0 " +
                    "      AND (:itemCode IS NULL OR ITM_CODE IN (:itemCode)) " +
                    "    GROUP BY UPPER(LTRIM(RTRIM(ITM_CODE))) " +
                    "), " +

                    "IVM_OS AS ( " +
                    "    SELECT " +
                    "        UPPER(LTRIM(RTRIM(ITM_CODE))) AS ITM_CODE, " +
                    "        SUM(MVT_QTY) AS IV_OS_QTY " +
                    "    FROM TBLINVENTORYMOVEMENT " +
                    "    WHERE C_ID = :companyCodeId " +
                    "      AND PLANT_ID = :plantId " +
                    "      AND LANG_ID = :languageId " +
                    "      AND WH_ID = :warehouseId " +
                    "      AND MVT_TYP_ID = 4 " +
                    "      AND SUB_MVT_TYP_ID = 1 " +
                    "      AND IM_CTD_ON BETWEEN :openingFrom AND :openingTo " +
                    "      AND IS_DELETED = 0 " +
                    "      AND (:itemCode IS NULL OR ITM_CODE IN (:itemCode)) " +
                    "    GROUP BY UPPER(LTRIM(RTRIM(ITM_CODE))) " +
                    "), " +

                    "PAL_CS AS ( " +
                    "    SELECT " +
                    "        UPPER(LTRIM(RTRIM(ITM_CODE))) AS ITM_CODE, " +
                    "        SUM(PA_CNF_QTY) AS PA_CS_QTY " +
                    "    FROM TBLPUTAWAYLINE " +
                    "    WHERE C_ID = :companyCodeId " +
                    "      AND PLANT_ID = :plantId " +
                    "      AND LANG_ID = :languageId " +
                    "      AND WH_ID = :warehouseId " +
                    "      AND PA_CTD_ON BETWEEN :closingFrom AND :closingTo " +
                    "      AND IS_DELETED = 0 " +
                    "      AND (:itemCode IS NULL OR ITM_CODE IN (:itemCode)) " +
                    "    GROUP BY UPPER(LTRIM(RTRIM(ITM_CODE))) " +
                    "), " +

                    "PUL_CS AS ( " +
                    "    SELECT " +
                    "        UPPER(LTRIM(RTRIM(ITM_CODE))) AS ITM_CODE, " +
                    "        SUM(PICK_CNF_QTY) AS PI_CS_QTY " +
                    "    FROM TBLPICKUPLINE " +
                    "    WHERE C_ID = :companyCodeId " +
                    "      AND PLANT_ID = :plantId " +
                    "      AND LANG_ID = :languageId " +
                    "      AND WH_ID = :warehouseId " +
                    "      AND PICK_CTD_ON BETWEEN :closingFrom AND :closingTo " +
                    "      AND IS_DELETED = 0 " +
                    "      AND (:itemCode IS NULL OR ITM_CODE IN (:itemCode)) " +
                    "    GROUP BY UPPER(LTRIM(RTRIM(ITM_CODE))) " +
                    "), " +

                    "IVM_CS AS ( " +
                    "    SELECT " +
                    "        UPPER(LTRIM(RTRIM(ITM_CODE))) AS ITM_CODE, " +
                    "        SUM(MVT_QTY) AS IV_CS_QTY " +
                    "    FROM TBLINVENTORYMOVEMENT " +
                    "    WHERE C_ID = :companyCodeId " +
                    "      AND PLANT_ID = :plantId " +
                    "      AND LANG_ID = :languageId " +
                    "      AND WH_ID = :warehouseId " +
                    "      AND IM_CTD_ON BETWEEN :closingFrom AND :closingTo " +
                    "      AND IS_DELETED = 0 " +
                    "      AND (:itemCode IS NULL OR ITM_CODE IN (:itemCode)) " +
                    "    GROUP BY UPPER(LTRIM(RTRIM(ITM_CODE))) " +
                    ") " +

                    "SELECT " +
                    "    TH.C_ID AS companyCodeId, " +
                    "    TH.PLANT_ID AS plantId, " +
                    "    TH.LANG_ID AS languageId, " +
                    "    TH.WH_ID AS warehouseId, " +

                    "    (COALESCE(IVS.IS_OS_QTY,0) + COALESCE(PAL_OS.PA_OS_QTY,0) + COALESCE(IVM_OS.IV_OS_QTY,0) - COALESCE(PUL_OS.PI_OS_QTY,0)) AS openingStock, " +

                    "    COALESCE(PAL_CS.PA_CS_QTY,0) AS inboundQty, " +
                    "    COALESCE(PUL_CS.PI_CS_QTY,0) AS outboundQty, " +
                    "    COALESCE(IVM_CS.IV_CS_QTY,0) AS stockAdjustmentQty, " +
                    "    COALESCE(IV.IV_QTY,0) AS systemInventory, " +

                    "    TH.ITM_CODE AS itemCode, " +
                    "    TH.TEXT AS itemDescription, " +
                    "    TH.C_TEXT AS companyDescription, " +
                    "    TH.PLANT_TEXT AS plantDescription, " +
                    "    TH.WH_TEXT AS warehouseDescription, " +

                    "    ((COALESCE(IVS.IS_OS_QTY,0) + COALESCE(PAL_OS.PA_OS_QTY,0) + COALESCE(IVM_OS.IV_OS_QTY,0) - COALESCE(PUL_OS.PI_OS_QTY,0)) " +
                    "     + COALESCE(PAL_CS.PA_CS_QTY,0) + COALESCE(IVM_CS.IV_CS_QTY,0) - COALESCE(PUL_CS.PI_CS_QTY,0)) AS closingStock, " +

                    "    (((COALESCE(IVS.IS_OS_QTY,0) + COALESCE(PAL_OS.PA_OS_QTY,0) + COALESCE(IVM_OS.IV_OS_QTY,0) - COALESCE(PUL_OS.PI_OS_QTY,0)) " +
                    "     + COALESCE(PAL_CS.PA_CS_QTY,0) + COALESCE(IVM_CS.IV_CS_QTY,0) - COALESCE(PUL_CS.PI_CS_QTY,0)) " +
                    "     - COALESCE(IV.IV_QTY,0)) AS variance " +

                    "FROM IV " +
                    "LEFT JOIN THR TH ON TH.ITM_CODE = IV.ITM_CODE " +
                    "LEFT JOIN IVS ON IV.ITM_CODE = IVS.ITM_CODE " +
                    "LEFT JOIN PAL_OS ON IV.ITM_CODE = PAL_OS.ITM_CODE " +
                    "LEFT JOIN PUL_OS ON IV.ITM_CODE = PUL_OS.ITM_CODE " +
                    "LEFT JOIN IVM_OS ON IV.ITM_CODE = IVM_OS.ITM_CODE " +
                    "LEFT JOIN PAL_CS ON IV.ITM_CODE = PAL_CS.ITM_CODE " +
                    "LEFT JOIN PUL_CS ON IV.ITM_CODE = PUL_CS.ITM_CODE " +
                    "LEFT JOIN IVM_CS ON IV.ITM_CODE = IVM_CS.ITM_CODE ",
            nativeQuery = true)
    List<HistoryReport> findTransactionHistoryReportV6(@Param("companyCodeId") String companyCodeId,
                                                       @Param("plantId") String plantId, @Param("languageId") String languageId,
                                                       @Param("warehouseId") String warehouseId, @Param("itemCode") List<String> itemCode,
                                                       @Param("openingFrom") Date openingFrom, @Param("openingTo") Date openingTo,
                                                       @Param("closingFrom") Date closingFrom, @Param("closingTo") Date closingTo
    );

    @Query(value = " WITH THR AS ( \n" +
            "                SELECT\n" +
            "                    I.C_ID,\n" +
            "                    I.PLANT_ID,\n" +
            "                    I.LANG_ID,\n" +
            "                    I.WH_ID,\n" +
            "                    I.ITM_CODE,\n" +
            "                    I.MFR_PART AS MFR_NAME,\n" +
            "                    I.TEXT,\n" +
            "                    I.C_TEXT,\n" +
            "                    I.PLANT_TEXT,\n" +
            "                    I.WH_TEXT\n" +
            "                FROM TBLIMBASICDATA1 I\n" +
            "                WHERE I.C_ID = :companyCodeId\n" +
            "                  AND I.PLANT_ID = :plantId\n" +
            "                  AND I.LANG_ID = :languageId\n" +
            "                  AND I.WH_ID = :warehouseId\n" +
            "                  AND I.IS_DELETED = 0\n" +
            "                  AND (:itemCode IS NULL OR I.ITM_CODE in (:itemCode))\n" +
            "                  AND (:manufacturerName IS NULL OR I.MFR_PART = :manufacturerName)\n" +
            "            ),\n" +
            "            \n" +
            "            IVS AS (\n" +
            "                SELECT\n" +
            "                    ITM_CODE,\n" +
            "                    MFR_NAME,\n" +
            "                    SUM(INV_QTY + ALLOC_QTY) AS IS_OS_QTY\n" +
            "                FROM tblinventorystock\n" +
            "                WHERE C_ID = :companyCodeId\n" +
            "                  AND PLANT_ID = :plantId\n" +
            "                  AND LANG_ID = :languageId\n" +
            "                  AND WH_ID = :warehouseId\n" +
            "                  AND BIN_CL_ID IN (1,7)\n" +
            "                  AND IS_DELETED = 0\n" +
            "                GROUP BY ITM_CODE, MFR_NAME\n" +
            "            ),\n" +
            "            \n" +
            "            IV AS (\n" +
            "                SELECT\n" +
            "                    ITM_CODE,\n" +
            "                    MFR_NAME,\n" +
            "                    SUM(REF_FIELD_4) AS IV_QTY\n" +
            "                FROM TBLINVENTORY\n" +
            "                WHERE C_ID = :companyCodeId\n" +
            "                  AND PLANT_ID = :plantId\n" +
            "                  AND LANG_ID = :languageId\n" +
            "                  AND WH_ID = :warehouseId\n" +
            "                  AND BIN_CL_ID IN (1, 2, 7)\n" +
            "                 AND inv_id in (select max(inv_id) from tblinventory" +
            "      where IS_DELETED = 0 group by ITM_CODE, PAL_CODE,ST_BIN,  \n" +
            "             MFR_NAME, BARCODE_ID, pack_barcode, PLANT_ID, WH_ID, C_ID, LANG_ID) \n" +
            "                  AND IS_DELETED = 0 and inv_qty > 0 \n" +
            "                GROUP BY ITM_CODE, MFR_NAME\n" +
            "            ),\n" +
            "            \n" +
            "            PAL_OS AS (\n" +
            "                SELECT\n" +
            "                    ITM_CODE,\n" +
            "                    MFR_NAME,\n" +
            "                    SUM(PA_CNF_QTY) AS PA_OS_QTY\n" +
            "                FROM TBLPUTAWAYLINE\n" +
            "                WHERE C_ID = :companyCodeId\n" +
            "                  AND PLANT_ID = :plantId\n" +
            "                  AND LANG_ID = :languageId\n" +
            "                  AND WH_ID = :warehouseId\n" +
            "                  AND STATUS_ID IN (20,24)\n" +
            "                  AND PA_CTD_ON BETWEEN :openingFrom AND :openingTo\n" +
            "                  AND IS_DELETED = 0\n" +
            "                GROUP BY ITM_CODE, MFR_NAME\n" +
            "            ),\n" +
            "            \n" +
            "            PUL_OS AS (\n" +
            "                SELECT\n" +
            "                    ITM_CODE,\n" +
            "                    MFR_NAME,\n" +
            "                    SUM(PICK_CNF_QTY) AS PI_OS_QTY\n" +
            "                FROM TBLPICKUPLINE\n" +
            "                WHERE C_ID = :companyCodeId\n" +
            "                  AND PLANT_ID = :plantId\n" +
            "                  AND LANG_ID = :languageId\n" +
            "                  AND WH_ID = :warehouseId\n" +
            "                  AND STATUS_ID IN (50,57,59)\n" +
            "                  AND PICK_CTD_ON BETWEEN :openingFrom AND :openingTo\n" +
            "                  AND IS_DELETED = 0\n" +
            "                GROUP BY ITM_CODE, MFR_NAME\n" +
            "            ),\n" +
            "            \n" +
            "            IVM_OS AS (\n" +
            "                SELECT\n" +
            "                    ITM_CODE,\n" +
            "                    MFR_PART,\n" +
            "                    SUM(MVT_QTY) AS IV_OS_QTY\n" +
            "                FROM TBLINVENTORYMOVEMENT\n" +
            "                WHERE C_ID = :companyCodeId\n" +
            "                  AND PLANT_ID = :plantId\n" +
            "                  AND LANG_ID = :languageId\n" +
            "                  AND WH_ID = :warehouseId\n" +
            "                  AND MVT_TYP_ID = 4\n" +
            "                  AND SUB_MVT_TYP_ID = 1\n" +
            "                  AND IM_CTD_ON BETWEEN :openingFrom AND :openingTo\n" +
            "                  AND IS_DELETED = 0\n" +
            "                GROUP BY ITM_CODE, MFR_PART\n" +
            "            ),\n" +
            "            \n" +
            "            PAL_CS AS (\n" +
            "                SELECT\n" +
            "                    ITM_CODE,\n" +
            "                    MFR_NAME,\n" +
            "                    SUM(PA_CNF_QTY) AS PA_CS_QTY\n" +
            "                FROM TBLPUTAWAYLINE\n" +
            "                WHERE PA_CTD_ON BETWEEN :closingFrom AND :closingTo AND IS_DELETED = 0 \n" +
            "                GROUP BY ITM_CODE, MFR_NAME\n" +
            "            ),\n" +
            "            \n" +
            "            PUL_CS AS (\n" +
            "                SELECT\n" +
            "                    ITM_CODE,\n" +
            "                    MFR_NAME,\n" +
            "                    SUM(PICK_CNF_QTY) AS PI_CS_QTY\n" +
            "                FROM TBLPICKUPLINE\n" +
            "                WHERE PICK_CTD_ON BETWEEN :closingFrom AND :closingTo AND IS_DELETED = 0 \n" +
            "                GROUP BY ITM_CODE, MFR_NAME\n" +
            "            ),\n" +
            "            \n" +
            "            IVM_CS AS (\n" +
            "                SELECT\n" +
            "                    ITM_CODE,\n" +
            "                    MFR_PART,\n" +
            "                    SUM(MVT_QTY) AS IV_CS_QTY\n" +
            "                FROM TBLINVENTORYMOVEMENT\n" +
            "                WHERE IM_CTD_ON BETWEEN :closingFrom AND :closingTo AND IS_DELETED = 0 \n" +
            "                GROUP BY ITM_CODE, MFR_PART\n" +
            "            )\n" +
            "            \n" +
            "            SELECT\n" +
            "                TH.C_ID            AS companyCodeId,\n" +
            "                TH.PLANT_ID        AS plantId,\n" +
            "                TH.LANG_ID         AS languageId,\n" +
            "                TH.WH_ID           AS warehouseId,\n" +
            "            \n" +
            "            /* OPENING STOCK */\n" +
            "            ROUND(\n" +
            "                (\n" +
            "                    COALESCE(IVS.IS_OS_QTY,0)\n" +
            "                  + COALESCE(PAL_OS.PA_OS_QTY,0)\n" +
            "                  + COALESCE(IVM_OS.IV_OS_QTY,0)\n" +
            "                  - COALESCE(PUL_OS.PI_OS_QTY,0)\n" +
            "                ),\n" +
            "                2\n" +
            "            ) AS openingStock,\n" +
            "            \n" +
            "            ROUND(COALESCE(PAL_CS.PA_CS_QTY,0), 2) AS inboundQty,\n" +
            "            \n" +
            "            ROUND(COALESCE(PUL_CS.PI_CS_QTY,0), 2) AS outboundQty,\n" +
            "\n" +
            "            ROUND(COALESCE(IVM_OS.IV_OS_QTY,0), 2) AS stockAdjustmentQty,\n" +
            "            \n" +
            "            ROUND(COALESCE(IV.IV_QTY,0), 2) AS systemInventory,\n" +
            "            \n" +
            "            TH.ITM_CODE AS itemCode,\n" +
            "            TH.TEXT AS itemDescription,\n" +
            "            TH.C_TEXT AS companyDescription,\n" +
            "            TH.PLANT_TEXT AS plantDescription,\n" +
            "            TH.WH_TEXT AS warehouseDescription,\n" +
            "            TH.MFR_NAME AS manufacturerName,\n" +
            "            \n" +
            "            /* CLOSING STOCK */\n" +
            "            ROUND(\n" +
            "                (\n" +
            "                    (\n" +
            "                        COALESCE(IVS.IS_OS_QTY,0)\n" +
            "                      + COALESCE(PAL_OS.PA_OS_QTY,0)\n" +
            "                      + COALESCE(IVM_OS.IV_OS_QTY,0)\n" +
            "                      - COALESCE(PUL_OS.PI_OS_QTY,0)\n" +
            "                    )\n" +
            "                    + COALESCE(PAL_CS.PA_CS_QTY,0)\n" +
            "                    - COALESCE(PUL_CS.PI_CS_QTY,0)\n" +
            "                ),\n" +
            "                2\n" +
            "            ) AS closingStock,\n" +
            "            \n" +
            "            ROUND(\n" +
            "                (\n" +
            "                    (\n" +
            "                        (\n" +
            "                            COALESCE(IVS.IS_OS_QTY,0)\n" +
            "                          + COALESCE(PAL_OS.PA_OS_QTY,0)\n" +
            "                          + COALESCE(IVM_OS.IV_OS_QTY,0)\n" +
            "                          - COALESCE(PUL_OS.PI_OS_QTY,0)\n" +
            "                        )\n" +
            "                        + COALESCE(PAL_CS.PA_CS_QTY,0)\n" +
            "                        -- + COALESCE(IVM_CS.IV_CS_QTY,0)\n" +
            "                        - COALESCE(PUL_CS.PI_CS_QTY,0)\n" +
            "                    )\n" +
            "                    - COALESCE(IV.IV_QTY,0)\n" +
            "                ),\n" +
            "                2\n" +
            "            ) AS variance \n" +
            "            FROM THR TH\n" +
            "            LEFT JOIN IVS     ON TH.ITM_CODE = IVS.ITM_CODE AND TH.MFR_NAME = IVS.MFR_NAME\n" +
            "            LEFT JOIN IV      ON TH.ITM_CODE = IV.ITM_CODE AND TH.MFR_NAME = IV.MFR_NAME\n" +
            "            LEFT JOIN PAL_OS  ON TH.ITM_CODE = PAL_OS.ITM_CODE AND TH.MFR_NAME = PAL_OS.MFR_NAME\n" +
            "            LEFT JOIN PUL_OS  ON TH.ITM_CODE = PUL_OS.ITM_CODE AND TH.MFR_NAME = PUL_OS.MFR_NAME\n" +
            "            LEFT JOIN IVM_OS  ON TH.ITM_CODE = IVM_OS.ITM_CODE AND TH.MFR_NAME = IVM_OS.MFR_PART\n" +
            "            LEFT JOIN PAL_CS  ON TH.ITM_CODE = PAL_CS.ITM_CODE AND TH.MFR_NAME = PAL_CS.MFR_NAME\n" +
            "            LEFT JOIN PUL_CS  ON TH.ITM_CODE = PUL_CS.ITM_CODE AND TH.MFR_NAME = PUL_CS.MFR_NAME\n" +
            "            LEFT JOIN IVM_CS  ON TH.ITM_CODE = IVM_CS.ITM_CODE AND TH.MFR_NAME = IVM_CS.MFR_PART ", nativeQuery = true)
    List<HistoryReport> findTransactionHistoryReportV10(
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


    // BF
    @Query(value = "\n" +
            "WITH stock AS (\n" +
            "    SELECT \n" +
            "        MAX(s.MATERIAL_NO) AS inventoryOwner, \n" +
            "        s.ITM_CODE,\n" +
            "        s.BARCODE_ID AS BARCODE,\n" +
            "        SUM(s.INV_QTY) qty\n" +
            "    FROM tblinventorystockbfs s\n" +
            "    WHERE s.IU_CTD_ON < :fromDate\n" +
            "      AND s.IS_DELETED = 0\n" +
            "      AND (COALESCE(:inventoryOwner, null) IS NULL OR (s.MATERIAL_NO IN (:inventoryOwner))) \n" +
            "\t\t\n" +
            "    GROUP BY s.ITM_CODE, s.BARCODE_ID\n" +
            "),\n" +
            "\n" +
            "--------------OPENING - INBOUND-------------------\n" +
            "openingInbound AS (\n" +
            "    SELECT \n" +
            "        gr.ITM_CODE,\n" +
            "        gr.BARCODE_ID AS BARCODE,\n" +
            "        SUM(gr.accept_qty + gr.damage_qty) qty\n" +
            "    FROM tblgrline gr\n" +
            "    WHERE gr.IS_DELETED = 0 AND gr.GR_CTD_ON < :fromDate\n" +
            "    AND  (COALESCE(:inventoryOwner, null) IS NULL OR (gr.MATERIAL_NO IN (:inventoryOwner))) \n" +
            "\n" +
            "    GROUP BY gr.ITM_CODE, gr.BARCODE_ID\n" +
            "),\n" +
            "\n" +
            "--------------OPENING - OUTBOUND-------------------\n" +
            "openingOutbound AS (\n" +
            "    SELECT \n" +
            "        ob.ITM_CODE,\n" +
            "        ob.PARTNER_ITEM_BARCODE AS BARCODE,\n" +
            "        SUM(ob.dlv_qty) qty\n" +
            "    FROM tbloutboundline ob\n" +
            "    WHERE ob.DLV_CTD_ON < :fromDate\n" +
            "      AND ob.IS_DELETED = 0\n" +
            "    AND  (COALESCE(:inventoryOwner, null) IS NULL OR (ob.MATERIAL_NO IN (:inventoryOwner))) \n" +
            "    AND ob.dlv_qty > 0 \n" +
            "    GROUP BY ob.ITM_CODE, ob.PARTNER_ITEM_BARCODE\n" +
            "),\n" +
            "\n" +
            "--------------INBOUND-------------------\n" +
            "inbound AS (\n" +
            "    SELECT \n" +
            "        gr.ITM_CODE,\n" +
            "        gr.BARCODE_ID AS BARCODE,\n" +
            "        SUM(gr.accept_qty + gr.damage_qty) qty\n" +
            "    FROM tblgrline gr\n" +
//            "    WHERE gr.GR_CTD_ON >= :fromDate AND gr.GR_CTD_ON < DATEADD(DAY,1, :toDate)\n" +
            "    WHERE gr.IS_DELETED = 0 AND gr.GR_CTD_ON between :fromDate AND :toDate \n" +
            "    AND  (COALESCE(:inventoryOwner, null) IS NULL OR (gr.MATERIAL_NO IN (:inventoryOwner))) \n" +
            "\n" +
            "    GROUP BY gr.ITM_CODE, gr.BARCODE_ID\n" +
            "),\n" +
            "\n" +
            "--------------OUTBOUND-------------------\n" +
            "outbound AS (\n" +
            "    SELECT \n" +
            "        ob.ITM_CODE,\n" +
            "        ob.PARTNER_ITEM_BARCODE AS BARCODE,\n" +
            "        SUM(ob.dlv_qty) qty\n" +
            "    FROM tbloutboundline ob\n" +
//            "    WHERE ob.DLV_CTD_ON >= :fromDate AND ob.DLV_CTD_ON < DATEADD(DAY,1, :toDate)\n" +
            "    WHERE ob.DLV_CTD_ON between :fromDate and :toDate \n" +
            "      AND ob.IS_DELETED = 0\n" +
            "    AND  (COALESCE(:inventoryOwner, null) IS NULL OR (ob.MATERIAL_NO IN (:inventoryOwner))) \n" +
            "    AND ob.dlv_qty > 0 \n" +
            "    GROUP BY ob.ITM_CODE, ob.PARTNER_ITEM_BARCODE\n" +
            "),\n" +
            "\n" +
            "--------------INVENTORY-------------------\n" +
            "inventory AS (\n" +
            "\n" +
            "select max(MFR_DATE) mfrDate, max(TEXT) itemText, max(UTD_ON) updatedOn, ITM_CODE, BARCODE_ID as BARCODE  from tblinventory \n" +
            "where INV_ID in (select max(inv_id) from tblinventory group by ITM_CODE, BARCODE_ID, PAL_CODE) \n" +
            "and IS_DELETED = 0 group by ITM_CODE, BARCODE_ID\n" +
            "), \n" +
            "keys AS (\n" +
            "    SELECT ITM_CODE,BARCODE FROM stock\n" +
            "    UNION\n" +
            "    SELECT ITM_CODE,BARCODE FROM openingInbound\n" +
            "    UNION\n" +
            "    SELECT ITM_CODE,BARCODE FROM openingOutbound\n" +
            "    UNION\n" +
            "    SELECT ITM_CODE,BARCODE FROM inbound\n" +
            "    UNION\n" +
            "    SELECT ITM_CODE,BARCODE FROM outbound\n" +
            ") \n" +
            "--------------FINAL SELECT-------------------\n" +
            "SELECT\n" +
            "    k.ITM_CODE AS itemCode,\n" +
            "    k.BARCODE AS batchNo,\n" +
            "    s.inventoryOwner AS CustomerName,\n" +
            "\n" +
            "    ISNULL(s.qty,0) + ISNULL(i.qty,0) - ISNULL(o.qty,0) AS openingStock,\n" +
            "    ISNULL(i.qty,0) AS OpeningInboundQty,\n" +
            "    ISNULL(o.qty,0) AS OpeningOutboundQty,\n" +
            "    ISNULL(ib.qty,0) AS inboundQty,\n" +
            "    ISNULL(ob.qty,0) AS outboundQty,\n" +
            "\n" +
            "    'CASE' AS uom,\n" +
            "    iv.mfrDate AS mfg,\n" +
            "    iv.itemText AS itemDescription,\n" +
            "    iv.updatedOn AS date,\n" +
            "\n" +
            "    (ISNULL(s.qty,0)+ISNULL(i.qty,0)-ISNULL(o.qty,0))\n" +
            "    + ISNULL(ib.qty,0)\n" +
            "    - ISNULL(ob.qty,0) AS closingStock\n" +
            "\n" +
            "FROM keys k\n" +
            "LEFT JOIN stock s ON k.ITM_CODE=s.ITM_CODE AND k.BARCODE=s.BARCODE\n" +
            "LEFT JOIN openingInbound i ON k.ITM_CODE=i.ITM_CODE AND k.BARCODE=i.BARCODE\n" +
            "LEFT JOIN openingOutbound o ON k.ITM_CODE=o.ITM_CODE AND k.BARCODE=o.BARCODE\n" +
            "LEFT JOIN inbound ib ON k.ITM_CODE=ib.ITM_CODE AND k.BARCODE=ib.BARCODE\n" +
            "LEFT JOIN outbound ob ON k.ITM_CODE=ob.ITM_CODE AND k.BARCODE=ob.BARCODE\n" +
            "LEFT JOIN inventory iv ON k.ITM_CODE=iv.ITM_CODE AND k.BARCODE=iv.BARCODE\n" +
            "WHERE (" +
            "   (ISNULL(s.qty,0) + ISNULL(i.qty,0) - ISNULL(o.qty,0)) <> 0 " +
            "   OR ISNULL(ib.qty,0) <> 0 " +
            "   OR ISNULL(ob.qty,0) <> 0 " +
            "   OR ((ISNULL(s.qty,0) + ISNULL(i.qty,0) - ISNULL(o.qty,0)) " +
            "       + ISNULL(ib.qty,0) - ISNULL(ob.qty,0)) <> 0 " +
            ") " +
            "ORDER BY itemCode,batchNo", nativeQuery = true)
    List<HistoryReport> findClosingStockV9(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
                                           @Param("inventoryOwner") List<String> inventoryOwner);

    @Query(value = " ;WITH THR AS ( \n" +
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
            "    FROM TBLINVENTORYSTOCKBFS\n" +
            "    WHERE C_ID = :companyCodeId\n" +
            "      AND PLANT_ID = :plantId\n" +
            "      AND LANG_ID = :languageId\n" +
            "      AND WH_ID = :warehouseId\n" +
            "      AND BIN_CL_ID IN (1,7)\n" +
            "      AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "IV AS ( " +
            " SELECT ITM_CODE, MFR_NAME, SUM(REF_FIELD_4) AS IV_QTY " +
            " FROM TBLINVENTORY t " +
            " WHERE t.C_ID = :companyCodeId " +
            " AND t.PLANT_ID = :plantId " +
            " AND t.LANG_ID = :languageId " +
            " AND t.WH_ID = :warehouseId " +
            " AND t.IS_DELETED = 0 " +
            " AND t.REF_FIELD_4 > 0 " +
            " AND t.INV_ID IN ( " +
            "     SELECT MAX(inv_id) " +
            "     FROM TBLINVENTORY " +
            "     WHERE IS_DELETED = 0 " +
            "       AND C_ID = :companyCodeId " +
            "       AND PLANT_ID = :plantId " +
            "       AND LANG_ID = :languageId " +
            "       AND WH_ID = :warehouseId " +
            "     GROUP BY itm_code,pal_code,barcode_id,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id) " +
//            "              MFR_NAME, ST_BIN, " +
//            "              PLANT_ID, WH_ID, C_ID, LANG_ID " +
//            " ) " +
            " GROUP BY ITM_CODE, MFR_NAME " +
            "), " +
            "\n" +
            "PAL_OS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_NAME,\n" +
            "        SUM(PA_CNF_QTY) AS PA_OS_QTY\n" +
            "    FROM TBLPUTAWAYLINE\n" +
            "    WHERE C_ID = :companyCodeId\n" +
            "      AND PLANT_ID = :plantId\n" +
            "      AND LANG_ID = :languageId\n" +
            "      AND WH_ID = :warehouseId\n" +
            "      AND STATUS_ID IN (20,24)\n" +
            "      AND PA_CTD_ON BETWEEN :openingFrom AND :openingTo\n" +
            "      AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "OBL_OS AS ( \n" +
            "SELECT ITM_CODE, \n" +
            " MFR_NAME, SUM(DLV_QTY) AS OB_OS_QTY FROM TBLOUTBOUNDLINE WHERE C_ID = :companyCodeId \n" +
            "AND PLANT_ID = :plantId \n" +
            "AND LANG_ID = :languageId \n" +
            "AND WH_ID = :warehouseId \n" +
            "AND STATUS_ID IN (50,57,59)\n" +
            " AND IS_DELETED = 0 \n" +
            "AND DLV_CTD_ON BETWEEN :openingFrom AND :openingTo \n" +
            "GROUP BY ITM_CODE, MFR_NAME \n" +
            "),\n" +
            "\n" +
            "IVM_OS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_PART,\n" +
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
            "    GROUP BY ITM_CODE, MFR_PART\n" +
            "),\n" +
            "\n" +
            "PAL_CS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_NAME,\n" +
            "        SUM(PA_CNF_QTY) AS PA_CS_QTY\n" +
            "    FROM TBLPUTAWAYLINE\n" +
            "    WHERE PA_CTD_ON BETWEEN :closingFrom AND :closingTo\n" +
            "AND IS_DELETED = 0\n" +
            "    GROUP BY ITM_CODE, MFR_NAME\n" +
            "),\n" +
            "\n" +
            "OBL_CS AS (\n" +
            " SELECT\n" +
            " ITM_CODE,\n" +
            "  MFR_NAME,\n" +
            " SUM(DLV_QTY) AS OB_CS_QTY\n" +
            " FROM TBLOUTBOUNDLINE\n" +
            "  WHERE\n" +
            "  IS_DELETED = 0\n" +
            "  AND DLV_CTD_ON BETWEEN :closingFrom AND :closingTo\n" +
            "  GROUP BY ITM_CODE, MFR_NAME\n" +
            "  ),\n" +
            "\n" +
            "IVM_CS AS (\n" +
            "    SELECT\n" +
            "        ITM_CODE,\n" +
            "        MFR_PART,\n" +
            "        SUM(MVT_QTY) AS IV_CS_QTY\n" +
            "    FROM TBLINVENTORYMOVEMENT\n" +
            "    WHERE IM_CTD_ON BETWEEN :closingFrom AND :closingTo\n" +
            "    GROUP BY ITM_CODE, MFR_PART\n" +
            ") ,\n" +
            " BASE AS (\n" +
            "SELECT ITM_CODE, MFR_NAME FROM IV\n" +
            "   UNION\n" +
            "  SELECT ITM_CODE, MFR_NAME FROM OBL_CS\n" +
            "   UNION\n" +
            "   SELECT ITM_CODE, MFR_NAME FROM PAL_CS\n" +
            "   UNION\n" +
            "   SELECT ITM_CODE, MFR_NAME FROM OBL_OS\n" +
            "   UNION\n" +
            "  SELECT ITM_CODE, MFR_NAME FROM PAL_OS\n" +
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
            "      - COALESCE(OBL_OS.OB_OS_QTY,0)\n" +

            "    ) AS openingStock,\n" +
            "\n" +
            "    COALESCE(PAL_CS.PA_CS_QTY,0) AS inboundQty,\n" +
            "    COALESCE(OBL_CS.OB_CS_QTY,0) AS outboundQty, "  +
            "    COALESCE(IVM_CS.IV_CS_QTY,0) AS stockAdjustmentQty,\n" +
            "    COALESCE(IV.IV_QTY,0)        AS systemInventory,\n" +
            "\n" +
            "    IV.ITM_CODE AS itemCode,\n" +
            "    TH.TEXT            AS itemDescription,\n" +
            "    TH.C_TEXT as companyDescription,\n" +
            "    TH.PLANT_TEXT as plantDescription,\n" +
            "    TH.WH_TEXT as warehouseDescription,\n" +
            "    IV.MFR_NAME AS manufacturerName,\n" +
            "\n" +
            "    /* CLOSING STOCK */\n" +
            "    (\n" +
            "        (\n" +
            "            COALESCE(IVS.IS_OS_QTY,0)\n" +
            "          + COALESCE(PAL_OS.PA_OS_QTY,0)\n" +
            "          + COALESCE(IVM_OS.IV_OS_QTY,0)\n" +
            "          - COALESCE(OBL_OS.OB_OS_QTY,0)\n" +


            "        )\n" +
            "        + COALESCE(PAL_CS.PA_CS_QTY,0)\n" +
            "        + COALESCE(IVM_CS.IV_CS_QTY,0)\n" +
            "        - COALESCE(OBL_CS.OB_CS_QTY,0) " +

            "    ) AS closingStock,\n" +
            "\n" +
            "    (\n" +
            "        (\n" +
            "            (\n" +
            "                COALESCE(IVS.IS_OS_QTY,0)\n" +
            "              + COALESCE(PAL_OS.PA_OS_QTY,0)\n" +
            "              + COALESCE(IVM_OS.IV_OS_QTY,0)\n" +
            "              - COALESCE(OBL_OS.OB_OS_QTY,0)\n" +

            "            )\n" +
            "            + COALESCE(PAL_CS.PA_CS_QTY,0)\n" +
            "            + COALESCE(IVM_CS.IV_CS_QTY,0)\n" +
            "            - COALESCE(OBL_CS.OB_CS_QTY,0) " +
            "        )\n" +
            "            - COALESCE(IV.IV_QTY,0)\n" +
            "    ) AS variance\n" +

            " FROM BASE B\n" +

            "LEFT JOIN THR TH\n" +
            "ON B.ITM_CODE = TH.ITM_CODE\n" +
            "AND ISNULL(B.MFR_NAME,'') = ISNULL(TH.MFR_NAME,'')\n" +

            "LEFT JOIN IV\n" +
            "ON B.ITM_CODE = IV.ITM_CODE\n" +
            "AND ISNULL(B.MFR_NAME,'') = ISNULL(IV.MFR_NAME,'')\n" +

            " LEFT JOIN IVS\n" +
            "ON B.ITM_CODE = IVS.ITM_CODE\n" +
            " AND ISNULL(B.MFR_NAME,'') = ISNULL(IVS.MFR_NAME,'')\n" +

            " LEFT JOIN PAL_OS\n" +
            "ON B.ITM_CODE = PAL_OS.ITM_CODE\n" +
            "AND ISNULL(B.MFR_NAME,'') = ISNULL(PAL_OS.MFR_NAME,'')\n" +

            " LEFT JOIN OBL_OS\n" +
            "ON B.ITM_CODE = OBL_OS.ITM_CODE\n" +
            " AND ISNULL(B.MFR_NAME,'') = ISNULL(OBL_OS.MFR_NAME,'')\n" +

            " LEFT JOIN IVM_OS\n" +
            "ON B.ITM_CODE = IVM_OS.ITM_CODE\n" +
            "AND ISNULL(B.MFR_NAME,'') = ISNULL(IVM_OS.MFR_PART,'')\n" +

            " LEFT JOIN PAL_CS\n" +
            "ON B.ITM_CODE = PAL_CS.ITM_CODE\n" +
            "AND ISNULL(B.MFR_NAME,'') = ISNULL(PAL_CS.MFR_NAME,'')\n" +

            "LEFT JOIN OBL_CS\n" +
            " ON B.ITM_CODE = OBL_CS.ITM_CODE\n" +
            " AND ISNULL(B.MFR_NAME,'') = ISNULL(OBL_CS.MFR_NAME,'')\n" +

            " LEFT JOIN IVM_CS\n" +
            "ON B.ITM_CODE = IVM_CS.ITM_CODE\n" +
            "AND ISNULL(B.MFR_NAME,'') = ISNULL(IVM_CS.MFR_PART,'')\n", nativeQuery = true)
    List<HistoryReport> findTransactionHistoryReportV9(
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