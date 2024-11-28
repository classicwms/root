package com.tekclover.wms.api.idmaster.model.user;

import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;
import lombok.Data;

import java.util.List;

@Data
public class Login {

    private List<UserManagement> users;
    private List<RoleAccess> userRole;
    private List<ModuleId> userModule;

}