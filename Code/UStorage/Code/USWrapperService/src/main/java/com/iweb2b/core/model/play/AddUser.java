package com.iweb2b.core.model.play;

import lombok.Data;

@Data
public class AddUser {

    public enum Role {USER, ADMIN, USER_MANAGER}

    private String username;
    private String password;
    private String email;
    private Role role;
    private String firstname;
    private String lastname;
    private String company;
    
    private String city;    
    private String state;    
    private String country;
}
