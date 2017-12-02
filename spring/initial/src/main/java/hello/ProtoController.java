package hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import proto.MotionCommandProto;
import proto.ProtoMessage;

@Controller
public class ProtoController
{

    @GetMapping("/proto")
    public String greetingForm(Model model)
    {
    	model.addAttribute("protoMessage", new ProtoMessage());
        return "proto";
    }

    @PostMapping("/proto")
    public String greetingSubmit(@ModelAttribute ProtoMessage protoMessage)
    {
        // This method receives the form data that was submitted to /proto.html
    	// Using the given data, construct a protobuf msg and send it over 0mq
    	// After that, consider writing this data to a DB
    	
    	System.out.println(protoMessage.getThrottle());
    	System.out.println(protoMessage.getServo());
    	
    	// construct motioncommandproto builder
    	MotionCommandProto.Command.Builder motionCmdBuilder = MotionCommandProto.Command.newBuilder();
    	motionCmdBuilder.setThrottle(protoMessage.getThrottle());
    	motionCmdBuilder.setServo(protoMessage.getServo());
    	
    	// protobuf messages are immutable; builders must be used to set the message fields
    	// building the builder object returns the proto msg with the set fields
    	MotionCommandProto.Command motionCmd = motionCmdBuilder.build();
    	
    	System.out.println("motionCmd.getThrottle: " + motionCmd.getThrottle());
    	System.out.println("motionCmd.getServo: " + motionCmd.getServo());    	
    	
    	// now send the proto message over a zmq socket
    	
    	return "proto";
    }

}
