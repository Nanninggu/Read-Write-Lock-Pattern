package com.example.Read_Write.Lock.Pattern.controller;

import com.example.Read_Write.Lock.Pattern.dto.User;
import com.example.Read_Write.Lock.Pattern.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable("id") int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/user/{id}")
    public int updateUser(@PathVariable("id") int id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return id;
    }
}
