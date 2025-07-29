package com.tekclover.wms.core.model.idmaster;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbluser")
public class User {

    public enum Role {USER, ADMIN, USER_MANAGER}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "uid")
    private Long id;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "firstname")
    private String firstname;
    
    @Column(name = "lastname")
    private String lastname;
    
    @Column(name = "company")
    private String company;
    
    @JsonIgnore
    @Column(name = "password")
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "country")
    private String country;
}
