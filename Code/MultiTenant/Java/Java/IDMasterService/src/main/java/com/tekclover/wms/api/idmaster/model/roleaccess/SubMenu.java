package com.tekclover.wms.api.idmaster.model.roleaccess;

import lombok.Data;

@Data
public class SubMenu {
	private Long subMenuId;
	private Boolean create;
	private Boolean edit;
	private Boolean view;
	private Boolean delete;
}