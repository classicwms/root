package com.tekclover.wms.api.outbound.transaction.model.integration;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblapiintegration")
public class IntegrationApiResponse {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REPONSE_ID") 
	private Long id;
	
	@Column(name = "ORDER_TYPE_ID") 
	private Long orderTypeId;
	
	@Column(name = "ORDER_NUMBER")
	private String orderNumber;
	
	@Column(name = "ORDER_TYPE")
	private String orderType; 	// Inbound/Outbound
	
	@Column(name = "RESPONSE_CODE")
	private String responseCode;
	
	@Column(name = "RESPONSE_TEXT")
	private String responseText;
	
	@Column(name = "TR_DATE")
	private Date transDate;
	
	@Column(name = "API_URL")
	private String apiUrl;
}
