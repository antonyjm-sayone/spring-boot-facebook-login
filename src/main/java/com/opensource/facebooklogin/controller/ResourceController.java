package com.opensource.facebooklogin.controller;

import com.opensource.facebooklogin.model.User;
import com.opensource.facebooklogin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class ResourceController {

    @Autowired
    private UserService userService;

    @GetMapping("/details")
    public ResponseEntity getUserDetails(Principal principal){
        User user = userService.getUser(principal.getName());
        if(user != null){
            return new ResponseEntity(user, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/update")
    public ResponseEntity updateUser(Principal principal){
        User user = userService.getUser(principal.getName());
        if(user != null){
            return new ResponseEntity(userService.createOrUpdateUser(user.getId(), user.getEmail(), "Renamed "+user.getName()), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

}
