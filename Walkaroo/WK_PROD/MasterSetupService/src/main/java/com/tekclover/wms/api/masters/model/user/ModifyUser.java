package com.tekclover.wms.api.masters.model.user;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ModifyUser {

    public enum Role {USER, ADMIN, USER_MANAGER}

    @NotBlank(message = "Name is mandatory")
    private String username;
    
    @NotBlank(message = "Email is mandatory")
    @Email
    private String email;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private String city;    
    private String state;    
    private String country;
}
