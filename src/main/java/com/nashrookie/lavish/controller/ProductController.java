package com.nashrookie.lavish.controller;

import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    @GetMapping()
    //@Secured("USER")
    public String getProducts(Principal principal) {
        return principal.getName();
    }
    
    // TODO: test security
    @PostMapping()
    public String testEndpoint(@RequestBody String entity) {
        Integer random = (int) (Math.random() * 1000);
        return "SUCCESS " + random;
    }
    
}
