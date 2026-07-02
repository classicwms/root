package com.tekclover.wms.api.enterprise.model.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblconfiguration")
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIG_ID")
    private Long configurationId;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
    private String languageId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(25)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
    private String plantId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;

    @Column(name = "PROFILE", columnDefinition = "nvarchar(100)")
    private String profile;

    @Column(name = "PRE_IBH", columnDefinition = "bit default '1'")
    private boolean preInboundHeader;

    @Column(name = "PRE_IBL", columnDefinition = "bit default '1'")
    private boolean preInboundLine;

    @Column(name = "IBH", columnDefinition = "bit default '1'")
    private boolean inboundHeader;

    @Column(name = "IBL", columnDefinition = "bit default '1'")
    private boolean inboundLine;

    @Column(name = "STGH", columnDefinition = "bit default '1'")
    private boolean stagingHeader;

    @Column(name = "STGL", columnDefinition = "bit default '1'")
    private boolean stagingLine;

    @Column(name = "GRH", columnDefinition = "bit default '1'")
    private boolean grHeader;

    @Column(name = "GRL", columnDefinition = "bit default '1'")
    private boolean grLine;

    @Column(name = "IB_QH", columnDefinition = "bit default '1'")
    private boolean inboundQualityHeader;

    @Column(name = "IB_QL", columnDefinition = "bit default '1'")
    private boolean inboundQualityLine;

    @Column(name = "PAH", columnDefinition = "bit default '1'")
    private boolean putAwayHeader;

    @Column(name = "PAL", columnDefinition = "bit default '1'")
    private boolean putAwayLine;

    @Column(name = "PRE_OBH", columnDefinition = "bit default '1'")
    private boolean preOutboundHeader;

    @Column(name = "PRE_OBL", columnDefinition = "bit default '1'")
    private boolean preOutboundLine;

    @Column(name = "OBH", columnDefinition = "bit default '1'")
    private boolean outboundHeader;

    @Column(name = "OBL", columnDefinition = "bit default '1'")
    private boolean outboundLine;

    @Column(name = "OMH", columnDefinition = "bit default '1'")
    private boolean orderManagementHeader;

    @Column(name = "OML", columnDefinition = "bit default '1'")
    private boolean orderManagementLine;

    @Column(name = "PUH", columnDefinition = "bit default '1'")
    private boolean pickupHeader;

    @Column(name = "PUL", columnDefinition = "bit default '1'")
    private boolean pickupLine;

    @Column(name = "OB_QH", columnDefinition = "bit default '1'")
    private boolean qualityHeader;

    @Column(name = "OB_QL", columnDefinition = "bit default '1'")
    private boolean qualityLine;

    @Column(name = "C_TXT", columnDefinition = "nvarchar(100)")
    private String companyDescription;

    @Column(name = "PLANT_TXT", columnDefinition = "nvarchar(100)")
    private String plantDescription;

    @Column(name = "WH_TXT", columnDefinition = "nvarchar(100)")
    private String warehouseDescription;

    @Column(name = "IS_DELETED", columnDefinition = "bigint default 0")
    private Long deletionIndicator;

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn;

    @Column(name = "UTD_BY", columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;
}