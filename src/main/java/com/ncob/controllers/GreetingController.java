package com.ncob.hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController
{

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="Dennis") String name, Model model) {
        
        String str = System.getProperty("controllers.dir"); //C:\Dennis\spring\gs-serving-web-content\initial
        String path = str + "\\src\\main\\resources\\static\\content\\dmoPic.jpg";
        System.out.println(path);
        /*
        model.addAttribute("firstName", name);
        model.addAttribute("path", path);
        */
        model.addAttribute("name", name);
        return "greeting";
    }
    
    @RequestMapping("/hello")
    public String hello(@RequestParam(value="name", required=false, defaultValue="Dennis") String name, Model model) {
        
        String str = System.getProperty("controllers.dir"); //C:\Dennis\spring\gs-serving-web-content\initial
        String path = str + "\\src\\main\\resources\\static\\content\\dmoPic.jpg";
        System.out.println(path);
        /*
        model.addAttribute("firstName", name);
        model.addAttribute("path", path);
        */
        model.addAttribute("name", name);
        return "hello";
    }

}