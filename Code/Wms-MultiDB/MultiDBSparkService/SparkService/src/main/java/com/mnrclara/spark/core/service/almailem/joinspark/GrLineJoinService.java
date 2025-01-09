package com.mnrclara.spark.core.service.almailem.joinspark;


import com.mnrclara.spark.core.model.Almailem.FindGrLineV2;
import com.mnrclara.spark.core.model.Almailem.joinspark.GrLineV3;
import com.mnrclara.spark.core.util.ConditionUtils;
import com.mnrclara.spark.core.util.DatabaseConnectionUtil;
import com.mnrclara.spark.core.util.SparkSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class GrLineJoinService {

    SparkSession sparkSession = SparkSessionUtil.createSparkSession();

    /**
     * FindGrLineV2
     *
     * @param findGrLineV2
     * @return
     */
    public List<GrLineV3> findGrLine(FindGrLineV2 findGrLineV2) {

        String query = "select gl.c_text, gl.plant_text, gl.wh_text, gl.status_text, gl.accept_qty, gl.damage_qty, gl.mfr_name, gl.itm_code, gl.barcode_id, " +
                " gl.ref_doc_no, gl.ref_doc_type, gl.ord_qty, gl.gr_cnf_on, gl.gr_ctd_by, gl.qty_type, gl.ref_field_10, gh.gr_ctd_on from tblgrline gl " +
                " left join (select  gr_no, ref_doc_no, c_id, plant_id, lang_id, wh_id, gr_ctd_on from tblgrheader where is_deleted = 0) gh ON gl.gr_no = gh.gr_no and " +
                " gl.ref_doc_no = gh.ref_doc_no and gl.c_id = gh.c_id and gl.plant_id = gh.plant_id and " +
                " gl.lang_id = gh.lang_id and gl.wh_id = gh.wh_id where gl.is_deleted = 0 ";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "gl.c_id", findGrLineV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "gl.lang_id", findGrLineV2.getLanguageId());
        ConditionUtils.addCondition(conditions, "gl.wh_id", findGrLineV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "gl.plant_id", findGrLineV2.getPlantId());
        ConditionUtils.addCondition(conditions, "gl.brand", findGrLineV2.getBrand());
        ConditionUtils.addCondition(conditions, "gl.barcode_id", findGrLineV2.getBarcodeId());
        ConditionUtils.numericConditions(conditions, "gl.ib_line_no", findGrLineV2.getLineNo());
        ConditionUtils.numericConditions(conditions, "gl.status_id", findGrLineV2.getStatusId());
        ConditionUtils.addCondition(conditions, "gl.case_code", findGrLineV2.getCaseCode());
        ConditionUtils.addCondition(conditions, "gl.itm_code", findGrLineV2.getItemCode());
        ConditionUtils.addCondition(conditions, "gl.st_bin_intm", findGrLineV2.getInterimStorageBin());
        ConditionUtils.addCondition(conditions, "gl.mfr_code", findGrLineV2.getManufacturerCode());
        ConditionUtils.addCondition(conditions, "gl.mfr_name", findGrLineV2.getManufacturerName());
        ConditionUtils.addCondition(conditions, "gl.pack_barcode", findGrLineV2.getPackBarcodes());
        ConditionUtils.addCondition(conditions, "gl.origin", findGrLineV2.getOrigin());
        ConditionUtils.addCondition(conditions, "gl.pre_ib_no", findGrLineV2.getPreInboundNo());
        ConditionUtils.addCondition(conditions, "gl.ref_doc_no", findGrLineV2.getRefDocNumber());
        ConditionUtils.addCondition(conditions, "gl.rej_reason", findGrLineV2.getRejectReason());
        ConditionUtils.addCondition(conditions, "gl.rej_type", findGrLineV2.getRejectType());
        ConditionUtils.numericConditions(conditions, "gl.ib_ord_typ_id", findGrLineV2.getInboundOrderTypeId());
        ConditionUtils.addDateCondition(conditions, "gl.gr_ctd_on", findGrLineV2.getStartCreatedOn(), findGrLineV2.getEndCreatedOn());

        if(!conditions.isEmpty()){
            query += " AND " + String.join( " AND ", conditions);
        }

        Properties conn = DatabaseConnectionUtil.getDatabaseConnectionProperties();
        String jdbcUrl = DatabaseConnectionUtil.getJdbcUrl();

        Dataset<Row> grLine = sparkSession.read()
                .option("fetchSize", "5000")
                .option("pushDownPredicate", true)
                .jdbc(jdbcUrl, "(" + query + ") as grl", conn);

        grLine.show();
        Encoder<GrLineV3> grLineV3Encoder = Encoders.bean(GrLineV3.class);
        Dataset<GrLineV3> resultData = grLine.as(grLineV3Encoder);

        return resultData.collectAsList();
    }



}
