package com.tekclover.wms.api.idmaster.model.roleaccess;

import java.util.List;

import lombok.Data;

@Data
public class Menu {
	private Long menuId;
	private List<SubMenu> subMenu;		
}
