package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(){
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model)
    {
        System.out.println("home page");

        //sending data to view
        model.addAttribute("name", "substring tech");
        model.addAttribute("YT", "Learn code");
        model.addAttribute("google", "https://www.google.com/");
        return "home";
    }

    //about route
    @RequestMapping("/about")
    public String aboutPage(Model model)
    {
        model.addAttribute("isLogin", false);
        System.out.println("about page loading");
        return "about";
    }

    //services route
    @RequestMapping("/services")
    public String servicesPage()
    {
        System.out.println("services page loading");
        return "services";
    }

    // Login route
    @GetMapping("/login")
    public String login() {
        return new String("login");
    }
    
    //signup route
    @GetMapping("/register")
    public String register(Model model) 
    {
        UserForm userForm = new UserForm();

        //for send the data manually
            // userForm.setName("Harsh");
            // userForm.setAbout("this is about write something about your self ");
        
        model.addAttribute("userForm", userForm);
        return new String("register");
    }

    // contact route
    @GetMapping("/contact")
    public String contacString() {
        return new String("contact");
    }
    
    //processing register
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult, HttpSession session)
    {
        System.out.println("processing register");
        System.out.println(userForm);

        if(rBindingResult.hasErrors())
            return "register";
        

     //this builder method not access the default values so we use and create normal object
        // User user = User.builder()
        // .name(userForm.getName())
        // .email(userForm.getEmail())
        // .password(userForm.getPassword())
        // .about(userForm.getAbout())
        // .phoneNumber(userForm.getPhoneNumber())
        // .build();

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setEnabled(false);
        user.setPhoneNumber(userForm.getPhoneNumber());

        userService.saveUser(user);

        Message message = Message.builder().content("Registration Successful").type(MessageType.blue).build();

        session.setAttribute("message", message);
        
        return "redirect:/register";
    }    
    

}
