package com.ustorage.core.model.masters;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private Long id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String company;
    private String password;
    private String role;
    private String city;
    private String state;
    private String country;

    private String userTypeId;
    private String phoneNo;
    private String status;

    private Long deletionIndicator = 0L;

    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
}
