package com.tekclover.wms.api.idmaster.model.roleaccess;

import lombok.Data;

import java.util.List;

@Data
public class FindRoleAccess {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> roleId;
    private List<String> languageId;
    private List<Long>menuId;
    private List<Long>subMenuId;
    private String menuName;
    private String subMenuName;
}
