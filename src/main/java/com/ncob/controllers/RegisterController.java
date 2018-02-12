package com.ncob.controllers;

import com.ncob.mongo.User;
import com.ncob.mongo.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegisterController
{
    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String register(Model model)
    {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute(name = "user") @Valid User user, BindingResult bindingResult, Model model)
    {
        if (bindingResult.hasErrors())
        {
            return "register";
        }
        else
        {
            try
            {
                // encrypt the pw so it is not stored in plain text
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepositoryImpl.registerUser(user);

                // success
                System.out.println("User registered");
                return "forward:/login.html";
            }
            catch(BadCredentialsException e)
            {
                // failure
                System.out.println("Username is already registered to an account");
                // registration was unsuccessful, clear email and try again
                // this is not the best/proper way to do this; look into error handling and form resubmission - what happens on page refresh?
                // display error message?
                user.setUsername("");
                user.setPassword("");
                // set a message that the username is already taken
                model.addAttribute("error", true);
                return "register";
            }

            /*
            if(userRepositoryImpl.registerUser(controllers) == 0)
            {
                // success
                System.out.println("User registered");
                // registration was successful, redirect to login page
                // do I need to clean up any objects here (model, view, etc)?
                //return "login";

                // for now, return blank register page

                // this is not the best/proper way to do this; look into error handling and form resubmission - what happens on page refresh?
//            controllers.setFirstName("");
//            controllers.setLastName("");
//            controllers.setEmail("");
//            controllers.setPassword("");
//            return "register";

                // this is better, but still not optimal I think
                return "redirect:/register.html";
            }
            else
            {
                // failure
                System.out.println("Username is already registered to an account");
                // registration was unsuccessful, clear email and try again
                // this is not the best/proper way to do this; look into error handling and form resubmission - what happens on page refresh?
                // display error message?
                controllers.setUsername("");
                controllers.setPassword("");
                return "register";
            }
            */
        }

    }
}
