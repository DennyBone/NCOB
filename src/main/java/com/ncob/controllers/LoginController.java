package com.ncob.controllers;

import com.ncob.mongo.User;
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

    @GetMapping("/home")
    public String homeForm(Model model)
    {
        model.addAttribute("pageTitle", "Home");

        return "home";
    }

    @GetMapping("/freelance")
    public String freelanceForm()
    {
        //model.addAttribute("pageTitle", "Home");

        return "freelance";
    }

}
