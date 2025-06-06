package com.scm.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    String fileName = UUID.randomUUID().toString();

    Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    public ContactService contactService;

    @RequestMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> logger.info(error.toString()));

            session.setAttribute("message", Message.builder()
                    .content("Please correc the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        String fileUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);

        Contact contact = new Contact();

        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setFavorite(contactForm.isFavorite());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setUser(user);
        contact.setPicture(fileUrl);
        contact.setCloudinaryImageOublicId(fileName);

        contactService.save(contact);
        System.out.println(contactForm);

        session.setAttribute("message", Message.builder()
                .content("You have successfully add the contact")
                .type(MessageType.green)
                .build());

        // session.setAttribute("message", "You have successfully add the contact");
        return "redirect:/user/contacts/add";
    }

    @RequestMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
            Authentication authentication) {
        String userName = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(userName);

        Page<Contact> pageContacts = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContacts", pageContacts);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    @RequestMapping("/search")
    public String searchHandler(
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {

        logger.info("field {} keyword {}", contactSearchForm.getField(), contactSearchForm.getValue());

        var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContacts = null;

        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContacts = contactService.searchByName(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContacts = contactService.searchByEmail(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContacts = contactService.searchByPhoneNumber(contactSearchForm.getValue(), size, page, sortBy,
                    direction, user);
        }
        logger.info("pageContacts {}", pageContacts);
        model.addAttribute("contactSearchForm", contactSearchForm);
        model.addAttribute("pageContacts", pageContacts);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        return "user/search";
    }

    @RequestMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") String contactId, HttpSession session) {
        contactService.delete(contactId);
        logger.info("contactId {} deleted", contactId);

        session.setAttribute("message",
                Message.builder()
                        .content("Conatact is deleted successfully !!")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts";
    }

    @GetMapping("/view/{contactId}")
    public String updateContactFormView(@PathVariable("contactId") String contactId, Model model) {
        var contact = contactService.getById(contactId);

        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());

        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);

        return "user/update_contact_view";
    }

    @RequestMapping(value="/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId")String contactId, @ModelAttribute ContactForm contactForm, BindingResult bindingResult, Model model)
    {
        if(bindingResult.hasErrors())
        {
            return "user/update_contact_view";
        }
        var con = contactService.getById(contactId); 
            
        con.setId(contactId);
        con.setName(contactForm.getName()); 
        con.setEmail(contactForm.getEmail()); 
        con.setAddress(contactForm.getAddress()); 
        con.setPhoneNumber(contactForm.getPhoneNumber()); 
        con.setDescription(contactForm.getDescription()); 
        con.setFavorite(contactForm.isFavorite()); 
        con.setWebsiteLink(contactForm.getWebsiteLink()); 
        con.setLinkedInLink(contactForm.getLinkedInLink()); 

        if(contactForm.getContactImage()!=null && contactForm.getContactImage().isEmpty() == false)
        {
            logger.info("file  is not empty");
            String fileName = UUID.randomUUID().toString(); 
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            con.setCloudinaryImageOublicId(fileName);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);
        }
        else
        {
            logger.info("file is empty");
        }

        var updatedCon = contactService.update(con);
        logger.info("updated Contact {}",updatedCon);
        model.addAttribute("message", Message.builder().content("Contact Updated").type(MessageType.green).build());

        return "redirect:/user/contacts/view/"+contactId;
    }

    
}
