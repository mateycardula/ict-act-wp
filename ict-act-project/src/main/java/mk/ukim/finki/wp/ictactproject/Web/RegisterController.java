package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.InvalidEmailOrPasswordException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.PasswordDoNotMatchException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.UsernameAlreadyExistsException;
import mk.ukim.finki.wp.ictactproject.Service.AuthService;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private final AuthService authService;
    private final MemberService memberService;

    public RegisterController(AuthService authService, MemberService memberService) {
        this.authService = authService;
        this.memberService = memberService;
    }

    @GetMapping
    public String getRegisterPage(@RequestParam(required = false) String error, Model model) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        model.addAttribute("types", PositionType.values());
        model.addAttribute("bodyContent", "register");
        return "master-template";
    }

    @PostMapping
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String repeatedPassword,
                           @RequestParam String name,
                           @RequestParam String surname,
                           @RequestParam String institution
    ) {
        try {
            this.memberService.register(email, password, repeatedPassword, name, surname, institution, PositionType.NEW_USER);
            return "redirect:/login";
        } catch (InvalidEmailOrPasswordException | PasswordDoNotMatchException |
                 UsernameAlreadyExistsException exception) {
            return "redirect:/register?error=" + exception.getMessage();
        }
    }

}
