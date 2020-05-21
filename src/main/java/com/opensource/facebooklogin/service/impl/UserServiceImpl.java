package com.opensource.facebooklogin.service.impl;

import com.opensource.facebooklogin.model.User;
import com.opensource.facebooklogin.repository.UserRepository;
import com.opensource.facebooklogin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createOrUpdateUser(String id, String email, String name){
        Optional<User> optUser = userRepository.findUserByEmail(email);
        if(optUser.isPresent()){
            User user = optUser.get();
            user.setName(name);
            return userRepository.save(user);
        }
        else{
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setEmail(email);
            return userRepository.save(user);
        }
    }

    public User getUser(String email){
        Optional<User> optUser = userRepository.findUserByEmail(email);
        if(optUser.isPresent()){
            return optUser.get();
        }

        return null;
    }
}
