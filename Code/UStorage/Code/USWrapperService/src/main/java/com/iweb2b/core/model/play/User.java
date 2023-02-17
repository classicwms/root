package com.iweb2b.core.model.play;

import lombok.Data;

@Data
public class User {

    public enum Role {USER, ADMIN, USER_MANAGER}

    private Long id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String company;
    private String password;
    private Role role;
    private String city;
    private String state;
    private String country;
}
