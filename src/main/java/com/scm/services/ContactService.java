package com.scm.services;

import com.scm.entities.Contact;
import com.scm.entities.User;

import java.util.*;

import org.springframework.data.domain.Page;

public interface ContactService {

    Contact save(Contact contact);

    Contact update(Contact contact);

    List<Contact> getAll();

    Contact getById(String id);

    void delete(String id);

    Page<Contact> searchByName(String name, int size, int page, String sortBy, String order, User user);

    Page<Contact> searchByEmail(String email, int size, int page, String sortBy, String order, User user);

    Page<Contact> searchByPhoneNumber(String phoneNumber, int size, int page, String sortBy, String order, User user);

    List<Contact> getByUserId(String userId);

    Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction);
    
}
