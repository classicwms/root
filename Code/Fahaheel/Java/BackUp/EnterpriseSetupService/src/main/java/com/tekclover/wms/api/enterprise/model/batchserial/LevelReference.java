package com.tekclover.wms.api.enterprise.model.batchserial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbllevelreference")
@Where(clause = "IS_DELETED=0")
public class LevelReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ST_MTD")
    private String storageMethod;
    @Column(name="LEVEL_REF")
    private String levelReference;
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
