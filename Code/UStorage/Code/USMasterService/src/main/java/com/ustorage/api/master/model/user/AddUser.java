package com.ustorage.api.master.model.user;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AddUser {

    @NotBlank(message = "Name is mandatory")
    private String username;
    
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Email
    private String email;

    private String role;
    
    private String firstname;
    private String lastname;
    private String company;
    
    private String city;    
    private String state;    
    private String country;

    private String userTypeId;
    private String phoneNo;
    private String status;
}
