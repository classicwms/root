package com.tekclover.wms.core.batch.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.tekclover.wms.core.batch.dto.Person;

public class PersonFieldSetMapper implements FieldSetMapper<Person> {

	@Override
	public Person mapFieldSet(FieldSet fieldSet) {
		return new Person(fieldSet.readLong("id"),
				fieldSet.readString("firstName"),
				fieldSet.readString("lastName"));
	}
}