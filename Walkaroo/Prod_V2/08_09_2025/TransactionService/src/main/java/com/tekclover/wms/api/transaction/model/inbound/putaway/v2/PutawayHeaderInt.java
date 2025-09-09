package com.tekclover.wms.api.transaction.model.inbound.putaway.v2;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity 
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblputawayheaderint")
public class PutawayHeaderInt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PA_ID")
	private Long paId;
	
	@Column(name = "REF_DOC_NUMBER")
    private String refDocNumber;
	
	@Column(name = "HU_SERIAL_NO")
    private String huSerialNo;
	
	@Column(name = "TYPE")
    private String type;
	
	@Column(name = "MESSAGE")
    private String message;
	
	@Column(name = "SAP_DOC_NO")
    private String sapDocumentNo;
	
	@Column(name = "MAT_DOC_DATE")
    private Date matDocDate;
	
	@ColumnDefault("0")
	@Column(name = "RE_RUN")
    private Long reRun = 0L;

}
