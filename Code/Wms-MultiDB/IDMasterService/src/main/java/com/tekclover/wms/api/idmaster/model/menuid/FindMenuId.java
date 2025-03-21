package com.tekclover.wms.api.idmaster.model.menuid;
import lombok.Data;
import java.util.List;

@Data
public class FindMenuId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> menuId;
    private List<Long> subMenuId;
    private List<Long> authorizationObjectId;
    private List<String> languageId;
}
