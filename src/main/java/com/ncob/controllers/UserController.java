package com.ncob.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController
{

    // redirect here on successful login
    @GetMapping("/user")
    public String userPage(Model model)
    {
        //model.addAttribute("user", new User());

        // for now just display the user view

        return "user";
    }

}
