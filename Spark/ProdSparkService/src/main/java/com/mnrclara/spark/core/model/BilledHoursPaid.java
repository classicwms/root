package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BilledHoursPaid {

	private List<String> clientId;
	private List<String> matterNumber;
	private List<String> timeKeeperCode;

	private Date fromPostingDate;
	private Date toPostingDate;
	private Date fromTimeTicketDate;
	private Date toTimeTicketDate;
}
