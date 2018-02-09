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
public class RobotController
{

    // link to here from home page
    @GetMapping("/user/robots")
    public String robots(Model model)
    {
        //model.addAttribute("user", new User());

        // get the user from the security context

        // check the db to see if they have any connected robots

        // if so, display connected robots in a table with checkboxes

        // if not, display a message directing user to add a robot

        return "robots";
    }

    // function to handle add a robot button click



    //@Autowired
    //private UserRepositoryImpl userRepositoryImpl;

    // use a mongo client instead of repo

}
