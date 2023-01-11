package com.ustorage.api.master.model.documentstorage;

import com.ustorage.api.master.sequence.BaseSequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbldocumentstorage")
@Where(clause = "IS_DELETED=0")
public class DocumentStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_document_storage")
    @GenericGenerator(name = "seq_document_storage", strategy = "com.ustorage.api.master.sequence.DefaultSequence", parameters = {
            @org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "DOC"),
            @org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%09d")})
    @Column(name = "DOCUMENT_NUMBER")
    private String documentNumber;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "FILE_Description")
    private String fileDescription;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator = 0L;

    @Column(name = "REF_FIELD_1")
    private String referenceField1;

    @Column(name = "REF_FIELD_2")
    private String referenceField2;

    @Column(name = "REF_FIELD_3")
    private String referenceField3;

    @Column(name = "REF_FIELD_4")
    private String referenceField4;

    @Column(name = "REF_FIELD_5")
    private String referenceField5;

    @Column(name = "CTD_BY")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn;

    @Column(name = "UPLOADED_BY")
    private String uploadedBy;

    @Column(name = "UPLOADED_ON")
    private Date uploadedOn;

    @Column(name = "UTD_BY")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;

}
