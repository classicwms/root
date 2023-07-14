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
		private Double isOsQty;
		private Double paOsQty;
		private Double paOsReQty;
		private Double piOsQty;
		private Double ivOsQty;
	 	private Double paCsQty;
	 	private Double paCsReQty;
	 	private Double piCsQty;
	 	private Double ivCsQty;
}
