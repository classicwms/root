package com.ustorage.core.model.masters;

import lombok.Data;

@Data
public class AddUser {

    private String username;
    private String password;
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
