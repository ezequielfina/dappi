package com.uade.tg.controllers;


import com.uade.tg.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {


    @Autowired
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


}
