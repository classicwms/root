package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

@Data
public class SubMenu {
	private Long subMenuId;
	private Boolean create;
	private Boolean edit;
	private Boolean view;
	private Boolean delete;
}