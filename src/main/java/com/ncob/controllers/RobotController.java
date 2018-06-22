package com.ncob.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    // test function that will open a dealer socket to the broker backend over inproc
    // eventually there will be functions for sending commands down and receiving telemetry from the robot
    // right now this function isn't hooked up to anything
    private void connectToBroker()
    {
        // create MqWorker(?) class that will provide methods for controllers to connect and talk to the broker
        // this way controllers can mimic 'workers'
    }

    // function to handle add a robot button click



    //@Autowired
    //private UserRepositoryImpl userRepositoryImpl;

    // use a mongo client instead of repo

}
