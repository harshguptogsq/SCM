package com.scm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.ContactRepo;
import com.scm.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService{

    @Autowired
    private ContactRepo contactRepo;

    @Override
    public void delete(String id) 
    {
        var contact = contactRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Contact not found with given id : "+id));
        contactRepo.delete(contact);
    }

    @Override
    public List<Contact> getAll() 
    {
        return contactRepo.findAll();
    }

    @Override
    public Contact getById(String id) 
    {
        return contactRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Contact not found with given id : "+id));
    }

    @Override
    public List<Contact> getByUserId(String userId) 
    {
        return contactRepo.findByUserId(userId);
    }

    @Override
    public Contact save(Contact contact) 
    {
        String contactId = UUID.randomUUID().toString();
        contact.setId(contactId);
        return contactRepo.save(contact);
    }

    @Override
    public Contact update(Contact contact) 
    {
        var contactOld = contactRepo.findById(contact.getId()).orElseThrow(()-> new ResourceNotFoundException("Contact not found"));

        contactOld.setName(contact.getName());
        contactOld.setEmail(contact.getEmail());
        contactOld.setAddress(contact.getAddress());
        contactOld.setPhoneNumber(contact.getPhoneNumber());
        contactOld.setDescription(contact.getDescription());
        contactOld.setPicture(contact.getPicture());
        contactOld.setFavorite(contact.isFavorite());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setLinkedInLink(contact.getLinkedInLink());
        contactOld.setCloudinaryImageOublicId(contact.getCloudinaryImageOublicId());

        return contactRepo.save(contactOld);
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction) 
    {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUser(user,pageable);
    }

    @Override
    public Page<Contact> searchByName(String name, int size, int page, String sortBy, String order, User user) 
    {   
        Sort sort = order.equals("desc")?Sort.by(sortBy).descending(): Sort.by(sortBy).ascending(); 
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndNameContaining(user, name, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(String email, int size, int page, String sortBy, String order, User user) 
    {
        Sort sort = order.equals("desc")?Sort.by(sortBy).descending(): Sort.by(sortBy).ascending(); 
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndEmailContaining(user, email, pageable);    
    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoneNumber, int size, int page, String sortBy, String order, User user) 
    {
        Sort sort = order.equals("desc")?Sort.by(sortBy).descending(): Sort.by(sortBy).ascending(); 
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUserAndPhoneNumberContaining(user, phoneNumber, pageable);
    }


}
