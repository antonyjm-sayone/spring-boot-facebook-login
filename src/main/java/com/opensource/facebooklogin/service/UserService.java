package com.opensource.facebooklogin.service;

import com.opensource.facebooklogin.model.User;

import java.security.Principal;

public interface UserService {

    public User createOrUpdateUser(String id, String email, String name);

    public User getUser(String email);
}
