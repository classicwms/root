package com.tekclover.wms.api.idmaster.model.roleaccess;

import lombok.Data;

import java.util.List;

@Data
public class FindRoleAccess {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> roleId;
    private List<String> languageId;
    private List<Long>menuId;
    private List<Long>subMenuId;
    private String menuName;
    private String subMenuName;
}
