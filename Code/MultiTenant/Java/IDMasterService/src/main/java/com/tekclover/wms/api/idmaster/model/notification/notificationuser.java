package com.tekclover.wms.api.idmaster.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class notificationuser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @NonNull
    private String username;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private List<Token> tokens;
}
