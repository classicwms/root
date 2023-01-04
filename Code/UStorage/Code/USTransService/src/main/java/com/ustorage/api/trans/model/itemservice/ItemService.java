package com.ustorage.api.trans.model.itemservice;

import java.util.Date;

import javax.persistence.*;

import com.ustorage.api.trans.sequence.BaseSequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblitemservice")
@Where(clause = "IS_DELETED=0")
public class ItemService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "WORK_ORDER_ID")
    private String workOrderId;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "ITEM_SERVICE_NAME")
    private String itemServiceName;

    @Column(name = "ITEM_SERVICE_QUANTITY")
    private Double itemServiceQuantity;

    @Column(name = "ITEM_SERVICE_UNIT_PRICE")
    private Double itemServiceUnitPrice;

    @Column(name = "ITEM_SERVICE_TOTAL")
    private Double itemServiceTotal;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator = 0L;

    @Column(name = "CTD_BY")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn;

    @Column(name = "UTD_BY")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;

}
