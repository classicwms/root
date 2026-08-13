package com.tekclover.wms.core.util;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "testuser")
@Data
@Entity
@NoArgsConstructor
public class User1 {

	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "dob")
	private Date dob;
	
	public User1(String id, String name, Date dob) {
		this.id = id;
		this.name = name;
		this.dob = dob;
	}
}
