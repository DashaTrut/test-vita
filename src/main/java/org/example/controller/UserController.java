package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @GetMapping("/all")
    public List<User> getAllUser(@RequestHeader("X-Sharer-User-Id") Integer id) {
        return userService.getAll(id);
    }

    @GetMapping("/search")
    public List<User> searchUser(@RequestHeader("X-Sharer-User-Id") Integer id, @RequestParam(required = false) String text) {
        return userService.searchUser(text, id);
    }


    @PatchMapping("/operator/{customer}")
    public User createOperator(@RequestHeader("X-Sharer-User-Id") Integer id, @PathVariable Integer customer) {
        return userService.createOperator(id, customer);
    }
}
