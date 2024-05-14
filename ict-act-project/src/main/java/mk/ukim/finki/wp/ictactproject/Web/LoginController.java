//package mk.ukim.finki.wp.ictactproject.Web;
//
//import jakarta.servlet.http.HttpServletRequest;
//import mk.ukim.finki.wp.ictactproject.Models.Member;
//import mk.ukim.finki.wp.ictactproject.Models.exceptions.InvalidArgumentsException;
//import mk.ukim.finki.wp.ictactproject.Models.exceptions.InvalidUserCredentialsException;
//import mk.ukim.finki.wp.ictactproject.Service.AuthService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/login")
//public class LoginController {
//    private final AuthService authService;
//
//    public LoginController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @GetMapping
//    public String getLoginPage(Model model) {
//        model.addAttribute("bodyContent", "login");
//        return "master-template";
//    }
//
//    @PostMapping
//    public String login(HttpServletRequest request, Model model) {
//        Member user = null;
//        try {
//            user = this.authService.login(request.getParameter("email"),
//                    request.getParameter("password"));
////            if(user == null) {
////                model.addAttribute("hasError", true);
////                model.addAttribute("error", "Invalid Email Or Password! Please try again.");
////                model.addAttribute("bodyContent", "login");
////                return "master-template";
////            }
//        } catch (InvalidUserCredentialsException | InvalidArgumentsException exception) {
//            model.addAttribute("hasError", true);
//            model.addAttribute("error", exception.getMessage());
//            model.addAttribute("bodyContent", "login");
//            return "master-template";
//        }
//
//        request.getSession().setAttribute("user", user); //OVA NE TREBA
//        return "redirect:/meetings";//TODO: redirect to Home page
//    }
//}
