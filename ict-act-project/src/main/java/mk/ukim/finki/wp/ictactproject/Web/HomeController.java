package mk.ukim.finki.wp.ictactproject.Web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping()
    public String getHomePage(Model model){
        model.addAttribute("bodyContent", "home");

        return "master-template";
    }
}
