package com.mnrclara.spark.core.service.overc360;

import com.mnrclara.spark.core.model.overc360.Consignment;
import com.mnrclara.spark.core.util.DatabaseConnectionUtil;
import com.mnrclara.spark.core.util.SparkSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class ConsignmentSparkService {

    // SparkSession
    SparkSession spark = SparkSessionUtil.createSparkSession();

    public List<Consignment> fetchConsignments() {

        String query = "SELECT tce.consignment_id, tce.lang_id, tce.lang_text, tce.c_id, tce.c_name, tce.status_id, tce.status_text, " +
                "tce.partner_id, tce.partner_type, tce.partner_name, tce.master_airway_bill, tce.no_of_piece_hawb, tce.partner_master_ab, " +
                "td.address_hub_code, td.account_id, td.email, td.company_name, td.name, td.phone, td.alternate_phone, " +
                "tpd.customs_value, tpd.tags, tpd.volume, tpd.weight, tpd.weight_unit, " +
                "tid.image_ref_id, tid.currency, tid.description, tid.piece_item_id, tid.quantity " +
                "FROM tblconsignment_entity tce " +
                "LEFT JOIN tbldestdetails td on tce.consignment_id = td.dest_detail_id " +
                "LEFT JOIN tblorigindetails tod on tce.consignment_id = tod.origin_id " +
                "LEFT JOIN tblreturndetails tor on tce.consignment_id = tor.return_id " +
                "LEFT JOIN tblpiecedetails tpd on tce.consignment_id = tpd.piece_details_id " +
                "LEFT JOIN tblitemdetails tid on tce.consignment_id = tid.item_details_id";

        Properties conn = DatabaseConnectionUtil.getOverCDatabaseConnectionProperties();
        String jdbcUrl = DatabaseConnectionUtil.getOverCJdbcUrl();

        Dataset<Row> consignment = spark.read()
                .option("fetchSize", "5000")
                .option("pushDownPredicate", true)
                .jdbc(jdbcUrl, "(" + query + ") as grl", conn);

        consignment.show();

        // Nesting destination, origin, and return details
        consignment = consignment.withColumn("destinationDetails", functions.struct(
                functions.col("address_hub_code"),
                functions.col("account_id"),
                functions.col("email"),
                functions.col("company_name"),
                functions.col("name"),
                functions.col("phone"),
                functions.col("alternate_phone")
        ));

        consignment = consignment.withColumn("originDetails", functions.struct(
                functions.col("address_hub_code"),
                functions.col("account_id"),
                functions.col("email"),
                functions.col("company_name"),
                functions.col("name"),
                functions.col("phone"),
                functions.col("alternate_phone")
        ));

        consignment = consignment.withColumn("returnDetails", functions.struct(
                functions.col("address_hub_code"),
                functions.col("account_id"),
                functions.col("email"),
                functions.col("company_name"),
                functions.col("name"),
                functions.col("phone"),
                functions.col("alternate_phone")
        ));

        consignment = consignment.groupBy("consignment_id")
                .agg(functions.collect_list(functions.struct(
                        functions.col("customs_value"),
                        functions.col("tags"),
                        functions.col("volume"),
                        functions.col("weight"),
                        functions.col("weight_unit")
                )).as("pieceDetails"));

        consignment = consignment.groupBy("piece_id")
                .agg(functions.collect_list(functions.struct(
                        functions.col("image_ref_id"),
                        functions.col("currency"),
                        functions.col("description"),
                        functions.col("piece_item_id"),
                        functions.col("quantity")
                )).as("itemDetails"));

        // Dropping the individual columns now that they are nested
        consignment = consignment.drop("address_hub_code", "account_id", "email", "company_name", "name", "phone", "alternate_phone",
                "customs_value", "tags", "volume", "weight", "weight_unit",
                "image_ref_id", "currency", "description", "piece_item_id", "quantity");

        // Convert Dataset<Row> to Dataset<Consignment>
        Encoder<Consignment> consignmentEncoder = Encoders.bean(Consignment.class);
        Dataset<Consignment> resultData = consignment.as(consignmentEncoder);
        resultData.show();

        return resultData.collectAsList();
    }


//    public List<Consignment> fetchConsignments() {
//        String query = "SELECT tce.consignment_id, tce.lang_id, tce.lang_text, tce.c_id, tce.c_name, tce.status_id, tce.status_text, tce.partner_id, " +
//                "tce.partner_type, tce.partner_name, tce.master_airway_bill, tce.no_of_piece_hawb, tce.partner_master_ab, " +
//                "td.address_hub_code as dest_address_hub_code, td.account_id as dest_account_id, td.email as dest_email, td.company_name as dest_company_name, td.name as dest_name, td.phone as dest_phone, td.alternate_phone as dest_alternate_phone, " +
//                "tod.address_hub_code as origin_address_hub_code, tod.account_id as origin_account_id, tod.email as origin_email, tod.company_name as origin_company_name, tod.name as origin_name, tod.phone as origin_phone, tod.alternate_phone as origin_alternate_phone, " +
//                "tor.address_hub_code as return_address_hub_code, tor.account_id as return_account_id, tor.email as return_email, tor.company_name as return_company_name, tor.name as return_name, tor.phone as return_phone, tor.alternate_phone as return_alternate_phone, " +
//                "tpd.customs_value, tpd.tags, tpd.volume, tpd.weight, tpd.weight_unit, " +
//                "tpd.piece_details_id as piece_details_id, " +
//                "tid.image_ref_id, tid.currency, tid.description, tid.piece_item_id, tid.quantity, tid.piece_details_id as item_piece_details_id " +
//                "FROM tblconsignment_entity tce " +
//                "LEFT JOIN tbldestdetails td on tce.consignment_id = td.dest_detail_id " +
//                "LEFT JOIN tblorigindetails tod on tce.consignment_id = tod.origin_id " +
//                "LEFT JOIN tblreturndetails tor on tce.consignment_id = tor.return_id " +
//                "LEFT JOIN tblpiecedetails tpd on tce.consignment_id = tpd.consignment_id " +
//                "LEFT JOIN tblitemdetails tid on tpd.piece_details_id = tid.piece_details_id";
//
//        Properties conn = DatabaseConnectionUtil.getOverCDatabaseConnectionProperties();
//        String jdbcUrl = DatabaseConnectionUtil.getOverCJdbcUrl();
//        Dataset<Row> consignment = spark.read()
//                .option("fetchSize", "5000")
//                .option("pushDownPredicate", true)
//                .jdbc(jdbcUrl, "(" + query + ") as grl", conn);
//
//        // Struct for destinationDetails
//        consignment = consignment.withColumn("destinationDetails", struct(
//                col("dest_address_hub_code").alias("addressHubCode"),
//                col("dest_account_id").alias("accountId"),
//                col("dest_email").alias("email"),
//                col("dest_company_name").alias("companyName"),
//                col("dest_name").alias("name"),
//                col("dest_phone").alias("phone"),
//                col("dest_alternate_phone").alias("alternatePhone")
//        ));
//
//        // Struct for originDetails
//        consignment = consignment.withColumn("originDetails", struct(
//                col("origin_address_hub_code").alias("addressHubCode"),
//                col("origin_account_id").alias("accountId"),
//                col("origin_email").alias("email"),
//                col("origin_company_name").alias("companyName"),
//                col("origin_name").alias("name"),
//                col("origin_phone").alias("phone"),
//                col("origin_alternate_phone").alias("alternatePhone")
//        ));
//
//        // Struct for returnDetails
//        consignment = consignment.withColumn("returnDetails", struct(
//                col("return_address_hub_code").alias("addressHubCode"),
//                col("return_account_id").alias("accountId"),
//                col("return_email").alias("email"),
//                col("return_company_name").alias("companyName"),
//                col("return_name").alias("name"),
//                col("return_phone").alias("phone"),
//                col("return_alternate_phone").alias("alternatePhone")
//        ));
//
//        // Aggregate itemDetails first
//        Dataset<Row> itemDetails = consignment.select(
//                        col("item_piece_details_id"),
//                        struct(
//                                col("image_ref_id").alias("imageRefId"),
//                                col("currency"),
//                                col("description"),
//                                col("piece_item_id").alias("pieceItemId"),
//                                col("quantity")
//                        ).alias("itemDetail")
//                ).groupBy("item_piece_details_id")
//                .agg(collect_list("itemDetail").alias("itemDetails"));
//
//        // Join itemDetails back to consignment
//        consignment = consignment.join(itemDetails, consignment.col("piece_details_id").equalTo(itemDetails.col("item_piece_details_id")), "left");
//
//        // Struct for pieceDetails including itemDetails
//        consignment = consignment.withColumn("pieceDetails", struct(
//                col("customs_value").alias("customsValue"),
//                col("tags"),
//                col("volume"),
//                col("weight"),
//                col("weight_unit").alias("weightUnit"),
//                col("itemDetails")
//        ));
//
//        // Dropping Unnecessary Columns
//        consignment = consignment.drop("dest_address_hub_code", "dest_account_id", "dest_email", "dest_company_name", "dest_name", "dest_phone", "dest_alternate_phone");
//        consignment = consignment.drop("origin_address_hub_code", "origin_account_id", "origin_email", "origin_company_name", "origin_name", "origin_phone", "origin_alternate_phone");
//        consignment = consignment.drop("return_address_hub_code", "return_account_id", "return_email", "return_company_name", "return_name", "return_phone", "return_alternate_phone");
//        consignment = consignment.drop("customs_value", "tags", "volume", "weight", "weight_unit");
//        consignment = consignment.drop("image_ref_id", "currency", "description", "piece_item_id", "quantity", "item_piece_details_id");
//
//        Encoder<Consignment> consignmentEncoder = Encoders.bean(Consignment.class);
//        Dataset<Consignment> resultData = consignment.as(consignmentEncoder);
//        resultData.show();
//        return resultData.collectAsList();
//    }

}
