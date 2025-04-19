package com.nashrookie.lavish.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    @GetMapping()
    //@Secured("USER")
    public String getProducts() {
        return "SUCCESS";
    }
    
    // TODO: test security
    @PostMapping()
    public String testEndpoint(@Param("name") String name) {
        return "SUCCESS";
    }
}
