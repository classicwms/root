package com.tekclover.wms.core.batch.mapper;

import java.util.Date;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.util.DateUtils;
import com.tekclover.wms.core.util.User1;

public class UserFieldSetMapper implements FieldSetMapper<User1> {

	@Override
	public User1 mapFieldSet(FieldSet fieldSet) {
		String sdate = fieldSet.readString("dob");
		Date date = DateUtils.convertStringToDate2(sdate);
		
		return new User1(
				fieldSet.readString("id"),
				fieldSet.readString("name"),
				date
			);
	}
}