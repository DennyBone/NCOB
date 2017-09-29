package hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProtoController
{

    @GetMapping("/proto")
    public String greetingForm(Model model)
    {
        System.out.println("GET");
    	model.addAttribute("protoMessage", new ProtoMessage());
        return "proto";
    }

    @PostMapping("/proto")
    public String greetingSubmit(@ModelAttribute ProtoMessage protoMessage)
    {
        // This method receives the form data that was submitted to /proto.html
    	// Using the given data, use protobuf to write to the addresses.dat file
    	// After that, consider writing this data to a DB instead
    	
    	System.out.println(protoMessage.getFirstName());
    	System.out.println(protoMessage.getLastName());
    	
    	return "proto";
    }

}
