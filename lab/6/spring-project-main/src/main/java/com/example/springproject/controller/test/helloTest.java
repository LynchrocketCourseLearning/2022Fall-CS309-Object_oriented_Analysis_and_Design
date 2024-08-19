package com.example.springproject.controller.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/OOAD")
public class helloTest {

    @GetMapping("/hello")
    public String hello(){
        return "Hello world";
    }

    @GetMapping("/lab")
    public String lab(){
        return "lab6";
    }
}
