package com.opensource.facebooklogin.service;

import com.opensource.facebooklogin.model.User;
import com.opensource.facebooklogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String email){
        Optional<User> optUser = userRepository.findUserByEmail(email);
        if(optUser.isPresent()){
            User user = optUser.get();
            return User.addAuthorities(user);
        }
        else{
            throw new UsernameNotFoundException("User with email id "+ email + " not found.");
        }
    }
}
