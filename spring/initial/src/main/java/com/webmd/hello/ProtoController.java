package com.webmd.hello;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import proto.ProtoMessage;

@Controller
@Slf4j
public class ProtoController {

    @GetMapping("/proto")
    public String greetingForm(Model model) {
        System.out.println("GET");
        model.addAttribute("protoMessage", new ProtoMessage());
        return "proto";
    }

    @PostMapping("/proto")
    public String greetingSubmit(@ModelAttribute(name = "protoMessage") ProtoMessage protoMessage) {
        // This method receives the form data that was submitted to /proto.html
        // Using the given data, use protobuf to write to the addresses.dat file
        // After that, consider writing this data to a DB instead

        log.info("Throttle {}", protoMessage.getThrottle());
        log.info("Servo {}", protoMessage.getServo());


        return "proto";
    }

}
