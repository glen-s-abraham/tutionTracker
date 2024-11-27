package com.edore.tutionTracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String getMethodName() {
        return "Welcome to admin dashboard";
    }

}
