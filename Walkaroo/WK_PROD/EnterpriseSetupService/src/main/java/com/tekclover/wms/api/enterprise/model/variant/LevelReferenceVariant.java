package com.tekclover.wms.api.enterprise.model.variant;

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
@Table(name = "tbllevelreferencevariant")
@Where(clause = "IS_DELETED=0")
public class LevelReferenceVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "VAR_ID")
    private String variantId;
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
