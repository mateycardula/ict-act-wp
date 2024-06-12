package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.InvalidArgumentsException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping()
    public String getAllMembers(Model model){
        List<Member> members = memberService.getAll();
        model.addAttribute("members", members);
        model.addAttribute("bodyContent", "all-members");

        return "master-template";
    }

    @GetMapping("/edit/{id}")
    public String getEditMemberPage(Model model, @PathVariable Long id){
        Member member;
        try{
            member = memberService.findById(id);
        } catch (InvalidArgumentsException exception){
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        //TODO: Add details for the member
        model.addAttribute("bodyContent", "edit-member");
        return "master-template";
    }

    @PostMapping("/edit/{id}")
    public String editMember(Model model,
                               @PathVariable Long id,
                               @RequestParam String email,
                               @RequestParam String name,
                               @RequestParam String password,
                               @RequestParam String repeatPassword,
                               @RequestParam String surname,
                               @RequestParam String institution,
                               @RequestParam PositionType role) {
        Member member;
        try {
            member = memberService.editMember(id, email, password, repeatPassword, name, surname, institution, role);
        } catch (Exception exception){ //TODO: Change exception
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        return "redirect:/members";
    }

    @PostMapping("/delete/member/{id}")
    public String deleteMember(@PathVariable Long id) {
        Member member = memberService.deleteMember(id);

        return "redirect:/members";
    }
}
