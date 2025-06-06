package com.scm.services;

import java.util.List;
import java.util.Optional;

import com.scm.entities.User;

public interface UserService {

    User saveUser(User user);

    Optional<User> getUserById(String id);

    Optional<User> updatUser(User user);

    void deleteUser(String id);

    boolean isUserExist(String userId);

    boolean isUserExistByEmail(String email);

    List<User> gatAllUsers();

    User getUserByEmail(String email);

    //add more methods related to user service[Logic]
}
