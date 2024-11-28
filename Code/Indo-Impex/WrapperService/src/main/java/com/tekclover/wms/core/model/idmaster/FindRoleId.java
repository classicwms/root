package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindRoleId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> roleId;
    private List<String> languageId;
    private List<Long>menuId;
    private List<Long>subMenuId;
}
