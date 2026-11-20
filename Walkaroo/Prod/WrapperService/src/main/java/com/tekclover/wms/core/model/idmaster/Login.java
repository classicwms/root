package com.tekclover.wms.core.model.idmaster;

import com.tekclover.wms.core.model.user.UserManagement;
import lombok.Data;

import java.util.List;

@Data
public class Login {

    private List<UserManagement> users;
    private List<RoleAccess> userRole;
    private List<ModuleId> userModule;

}