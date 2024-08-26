package com.favoriteplace.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping()
    public String health(){
        return "hello success!";
    }

}