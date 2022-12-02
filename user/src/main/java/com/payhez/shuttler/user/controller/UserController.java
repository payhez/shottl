package com.payhez.shuttler.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {

    @GetMapping("/")
    public void testMethod2(){
        System.out.println("DENEME-TEST1");
    }

    @GetMapping("/test")
    public void testMethod(){
        System.out.println("DENEME-TEST");
    }
}
