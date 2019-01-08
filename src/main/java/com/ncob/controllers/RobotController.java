package com.ncob.controllers;

import com.ncob.mongo.robots.Robot;
import com.ncob.mongo.robots.RobotRepository;
import com.ncob.mongo.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class RobotController
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RobotRepository robotRepository;

    // link to here from home page
    @GetMapping("/user/robots")
    public String robots(Model model, Authentication authentication)
    {
        //add form backing object to model
        if (!model.containsAttribute("robot"))
            model.addAttribute("robot", new Robot());

        // get the user from the security context
        String userName = authentication.getName();
        // check the db to see if they have any connected robots
        List<String> robotList = userRepository.findByUsername(userName).getUserRobots();
        // if so, display connected robots in a table
        if (!model.containsAttribute("robotList"))
            model.addAttribute("robotList", robotList);

        // if not, display a message directing user to add a robot

        return "robots";
    }

    @PostMapping("/registerRobot")
    public String registerRobot(@ModelAttribute(name = "robot") @Valid Robot robot, BindingResult bindingResult,
                                Model model, Authentication authentication, RedirectAttributes redirectAttributes)
    {
        if (bindingResult.hasErrors())
        {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.robot", bindingResult);
            redirectAttributes.addFlashAttribute("robot", robot);
            return "redirect:/user/robots";
        }
        else
        {
            try
            {
                String username = authentication.getName();
                userRepository.addUserRobot(username, robot.getRobotName());
                robot.setPrimaryUser(username);
                robotRepository.registerRobot(robot);

                // success
                log.info("Robot registered");
                redirectAttributes.addFlashAttribute("success", true);
                return "redirect:/user/robots";
            }
            catch (BadCredentialsException e)
            {
                // failure
                log.info("Robot name is already registered to an account");
                redirectAttributes.addFlashAttribute("robot", robot);
                redirectAttributes.addFlashAttribute("error", true);
                return "redirect:/user/robots";
            }
        }
    }

    // test function that will open a dealer socket to the broker backend over inproc
    // eventually there will be functions for sending commands down and receiving telemetry from the robot
    // right now this function isn't hooked up to anything
    private void connectToBroker()
    {
        // create MqWorker(?) class that will provide methods for controllers to connect and talk to the broker
        // this way controllers can mimic 'workers'
    }

}
