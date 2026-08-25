package com.tekclover.wms.api.inbound.orders.model.common;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "db_config")
public class DbConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_id")
    private Long dbId;

    @Column(name = "db_name",nullable = false)
    private String dbName;

    @Column(name = "C_ID", columnDefinition = "nvarchar(25)")
    private String companyCode;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
    private String plantId;

    @Column(name = "WAREHOUSE_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;

}
