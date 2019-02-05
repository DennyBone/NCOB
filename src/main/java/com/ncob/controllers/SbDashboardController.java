package com.ncob.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class SbDashboardController
{
    @GetMapping("/user/robots/robot-dashboard/blank")
    public String blank(Model model, Authentication authentication)
    {

        return "sb-dashboard/sb-blank";
    }

    @GetMapping("/user/robots/robot-dashboard/404")
    public String pageNotFound(Model model, Authentication authentication)
    {

        return "sb-dashboard/sb-404";
    }

    @GetMapping("/user/robots/robot-dashboard/charts")
    public String charts(Model model, Authentication authentication)
    {

        return "sb-dashboard/sb-charts";
    }

    @GetMapping("/user/robots/robot-dashboard/forgot-password")
    public String forgotPassword(Model model, Authentication authentication)
    {

        return "sb-dashboard/sb-forgot-password";
    }

    @GetMapping("/user/robots/robot-dashboard/index")
    public String index(Model model, Authentication authentication)
    {

        return "sb-dashboard/sb-index";
    }

    @GetMapping("/user/robots/robot-dashboard/login")
    public String login(Model model, Authentication authentication)
    {

        return "sb-dashboard/sb-login";
    }

    @GetMapping("/user/robots/robot-dashboard/register")
    public String register(Model model, Authentication authentication)
    {

        return "sb-dashboard/sb-register";
    }

    @GetMapping("/user/robots/robot-dashboard/tables")
    public String tables(Model model, Authentication authentication)
    {

        return "sb-dashboard/sb-tables";
    }
}
