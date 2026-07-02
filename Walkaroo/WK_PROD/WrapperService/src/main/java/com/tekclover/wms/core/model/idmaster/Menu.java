package com.tekclover.wms.core.model.idmaster;

import java.util.List;

import lombok.Data;

@Data
public class Menu {
	private Long menuId;
	private List<SubMenu> subMenu;		
}
