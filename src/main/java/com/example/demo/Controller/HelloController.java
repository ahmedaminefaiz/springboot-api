package com.example.demo.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "https://ahmedaminefaiz.github.io/angular-frontend/")
public class HelloController {

    @GetMapping("/api/hello")
    public String sayHello() {
        return "Hello, Spring Boot API! test ";
    }
}
