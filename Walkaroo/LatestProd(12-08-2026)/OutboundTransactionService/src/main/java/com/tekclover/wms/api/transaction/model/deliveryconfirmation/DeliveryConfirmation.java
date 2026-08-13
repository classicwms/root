package com.tekclover.wms.api.transaction.model.deliveryconfirmation;

import com.tekclover.wms.api.transaction.model.deliveryline.DeliveryLineCompositeKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table( name = "tbldeliveryconfirmation")
public class DeliveryConfirmation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "DELIVERY_ID")
        private Long deliveryId;

        @Column(name = "LANG_ID", columnDefinition = "nvarchar(50)")
        private String languageId;

        @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
        private String companyCodeId;

        @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
        private String plantId;

        @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
        private String warehouseId;

        @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
        private String loginUserId;

        @Column(name = "ORDER_RECEIVED_ON")
        private Date orderReceivedOn;

        @Column(name = "ORDER_PROCESSED_ON")
        private Date orderProcessedOn;

        @Column(name = "process_status_id", columnDefinition = "bigint default'0'")
        private Long processedStatusId;

        @Column(name = "OUTBOUND", columnDefinition = "nvarchar(50)")
        private String outbound;

        @Column(name = "CUST_CODE", columnDefinition = "nvarchar(50)")
        private String customerCode;

        @Column(name = "CUST_NAME", columnDefinition = "nvarchar(100)")
        private String customer;

        @Column(name = "SKU_CODE", columnDefinition = "nvarchar(200)")
        private String skuCode;

        @Column(name = "MATERIAL", columnDefinition = "nvarchar(100)")
        private String material;

        @Column(name = "PRICE_SGMT", columnDefinition = "nvarchar(50)")
        private String priceSegment;

        @Column(name = "ARTICLE_NO", columnDefinition = "nvarchar(50)")
        private String articleNumber;

        @Column(name = "GENDER", columnDefinition = "nvarchar(50)")
        private String gender;

        @Column(name = "COLOR", columnDefinition = "nvarchar(50)")
        private String color;

        @Column(name = "SIZE", columnDefinition = "nvarchar(50)")
        private String size;

        @Column(name = "NO_OF_PAIRS", columnDefinition = "nvarchar(50)")
        private String noOfPairs;

        @Column(name = "HU_SERIAL_NO", columnDefinition = "nvarchar(100)")
        private String huSerialNo;

        @Column(name = "PIK_QTY", columnDefinition = "nvarchar(50)")
        private Double pickedQty;

        @Column(name = "PLANT")
        private String plant;

        @Column(name = "STORAGE_LOCATION", columnDefinition = "nvarchar(50)")
        private String storageLocation;

        @Column(name = "REMARK", columnDefinition = "nvarchar(1000)")
        private String remark;
}
