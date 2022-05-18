package com.tekclover.wms.api.idmaster.model.vertical;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddVertical {

	@NotNull(message = "Vertical Id is mandatory")
    private Long verticalId;
	
	private String verticalName;
	
	@NotBlank(message = "Language is mandatory")
	private String languageId;
	
	private String remark;
	private String createdBy;
}
