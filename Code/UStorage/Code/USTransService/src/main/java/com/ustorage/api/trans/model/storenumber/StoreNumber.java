package com.ustorage.api.trans.model.storenumber;

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
@Table(name = "tblstorenumber")
@Where(clause = "IS_DELETED=0")
public class StoreNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "STORE_NUMBER")
    private String storeNumber;

    @Column(name = "AGREEMENT_NUMBER")
    private String agreementNumber;

    @Column(name = "Description")
    private String description;

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
