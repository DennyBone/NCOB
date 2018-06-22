package com.ncob.controllers;

import com.ncob.mongo.users.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController
{

    @GetMapping("/login")
    public String getLoginView(Model model)
    {
        model.addAttribute("user", new User());
        return "login";
    }

    // If authenticated, return a different view

    @GetMapping("/home")
    public String homeForm()
    {
        return "home";
    }

    /*
    * this endpoint is not needed as the login.html form posts
    * to the /loginProcessor endpoint configured by spring security
    *
    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute(name = "user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("binding result error");
            return "loginTemplate";
        } else {
            // verify credentials
            return "greeting";
            //return "home";
        }
    }
    */
}
