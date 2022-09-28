package com.example.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.vo.Greeting;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired // 이처럼 직접 주입 받는 것은 권장하지 않는다.
    private Greeting greeting;

    private Environment env;

    @Autowired
    public UserController(Environment env) {
        this.env = env;
    }

    @GetMapping("health-check")
    public String status() {
        return "It's Working in User Service";
    }

    @GetMapping("welcome")
    public String welcome() {
        // return env.getProperty("greeting.message");
        return greeting.getMessage();
    }
}
