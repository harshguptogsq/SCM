package com.scm.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User, String>{

    // 1.extra db related methods , 
    // 2.custom query methods, 
    // 3. custpm finder methods

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);    
    Optional<User> findByEmailToken(String id);
} 
