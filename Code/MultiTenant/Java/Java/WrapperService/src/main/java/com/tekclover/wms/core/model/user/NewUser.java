package com.tekclover.wms.core.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tblnewuserreg")
@Data
public class NewUser {
	
	@Id
    @Column(name = "REG_ID")
	private String registerId;
	
	@Column(name = "CLIENT_NAME")
	private String clientName;
	
	@Column(name = "CLIENT_SECRET_ID")
	private String clientSecretId;
}
