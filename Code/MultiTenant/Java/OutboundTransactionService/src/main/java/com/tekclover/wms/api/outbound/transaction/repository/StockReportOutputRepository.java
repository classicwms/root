package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.report.StockReportOutput;
import com.tekclover.wms.api.outbound.transaction.model.report.StockReportRes;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface StockReportOutputRepository extends JpaRepository<StockReportOutput, Long>,
        JpaSpecificationExecutor<StockReportOutput>,
        StreamableJpaSpecificationRepository<StockReportOutput> {

    @Transactional
    @Procedure(procedureName = "sp_stock_report")
    void updateSpStockReport(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("itemText") String itemText,
            @Param("stockTypeText") String stockTypeText
    );

    @Transactional
    @Procedure(procedureName = "sp_stock_report_inv_proc")
    void updateSpStockReportV7(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("itemText") String itemText,
            @Param("stockTypeText") String stockTypeText
    );

    @Transactional
    @Procedure(procedureName = "sp_stock_report_inv_proc")
    void updateSpStockReportV5(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("itemText") String itemText,
            @Param("stockTypeText") String stockTypeText
    );

    @Query(value = "SELECT \n" +
            "    ib.c_id AS companyCodeId, ib.plant_id AS plantId,\n " +
            "    ib.lang_id AS languageId, ib.wh_id AS warehouseId,\n " +
            "    ib.itm_code AS itemCode, ib.mfr_part AS manufacturerName,\n " +
            "    ib.text AS itemText,\n " +
            "    COALESCE(SUM(inv.inv_qty), 0) AS invQty,\n " +
            "    COALESCE(SUM(inv.alloc_qty), 0) AS allocQty,\n " +
            "    COALESCE(SUM(inv.ref_field_4), 0) AS totalQty \n  " +
            " FROM tblimbasicdata1 ib \n " +
            " LEFT JOIN tblinventory inv\n" +
            "    ON inv.inv_id = (SELECT MAX(i2.inv_id) \n " +
            "        FROM tblinventory i2 \n " +
            "        WHERE i2.c_id = ib.c_id  AND i2.plant_id = ib.plant_id \n " +
            "          AND i2.lang_id = ib.lang_id  AND i2.wh_id = ib.wh_id \n " +
            "          AND i2.itm_code = ib.itm_code  AND i2.mfr_name = ib.mfr_part ) \n" +
            " WHERE \n " +
            "    ib.c_id = :companyCodeId AND ib.plant_id = :plantId \n " +
            "    AND ib.lang_id = :languageId  AND ib.wh_id = :warehouseId \n " +
            "    AND ib.is_deleted = 0 AND (:itemCode = '0' OR ib.itm_code = :itemCode) \n " +
            "    AND (:manufacturerName = '0' OR ib.mfr_part = :manufacturerName) \n " +
            "    AND (:itemText = '0' OR ib.text = :itemText) \n " +
            "    AND (:stockTypeText = '0' OR inv.bin_cl_id = CAST(:stockTypeText AS INT)) \n " +
            " GROUP BY ib.c_id, ib.plant_id, ib.lang_id, ib.wh_id, ib.itm_code,\n " +
            " ib.mfr_part, ib.text, ib.c_text, ib.plant_text, ib.wh_text ", nativeQuery = true)
    List<StockReportRes> updateStockReportV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("itemText") String itemText,
            @Param("stockTypeText") String stockTypeText);
}