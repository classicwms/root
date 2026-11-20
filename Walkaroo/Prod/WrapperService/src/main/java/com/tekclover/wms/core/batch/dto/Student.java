package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class Student {
	private long id;
	private String firstName;
	private String lastName;
	
	public Student (long id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}
} 
