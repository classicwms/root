package com.tekclover.wms.api.transaction.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltransactionhistoryresults"
//		,
//		uniqueConstraints = {
//		@UniqueConstraint (
//				name = "unique_key_transactionhistoryresults",
//				columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_CODE"})
//}
)
//@IdClass(TransactionHistoryCompositeKey.class)
public class TransactionHistoryResults {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

//		@Id
//		@Column(name = "LANG_ID")
//		private String languageId;
//
//		@Id
//		@Column(name = "C_ID")
//		private String companyCode;
//
//		@Id
//		@Column(name = "PLANT_ID")
//		private String plantId;

    //		@Id
//		@Column(name = "WH_ID")
    private String warehouseId;

    //		@Id
//		@Column(name = "ITM_CODE")
    private String itemCode;

    private String description;
    private Double isOsQty = 0D;
    private Double paOsQty = 0D;
    private Double paOsReQty = 0D;
    private Double piOsQty = 0D;
    private Double ivOsQty = 0D;
    private Double paCsQty = 0D;
    private Double paCsReQty = 0D;
    private Double piCsQty = 0D;
    private Double ivCsQty = 0D;

    private Double closingStock = 0D;
    private Double openingStock = 0D;
    private Double inboundQty = 0D;
    private Double outboundQty = 0D;
    private Double stockAdjustmentQty = 0D;
}
