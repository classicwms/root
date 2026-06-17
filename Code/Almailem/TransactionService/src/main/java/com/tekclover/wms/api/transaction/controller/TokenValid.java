package com.tekclover.wms.api.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/auth")
@RestController
public class TokenValid {

    @GetMapping("/validate")
    public ResponseEntity<String> validate() {
        return ResponseEntity.ok("VALID");
    }
}
