package com.opensource.facebooklogin.repository;

import com.opensource.facebooklogin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, String> {

    public Optional<User> findUserByEmail(String email);

}
