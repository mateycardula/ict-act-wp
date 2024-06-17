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
        member = memberService.findById(id);
        model.addAttribute("member", member);
        model.addAttribute("bodyContent", "edit-member");
        model.addAttribute("types", PositionType.values());
        return "master-template";
    }

    @PostMapping("/edit/{id}")
    public String editMember(Model model,
                               @PathVariable Long id,
//                               @RequestParam String email,
                               @RequestParam String name,
//                               @RequestParam String password,
//                               @RequestParam String repeatPassword,
                               @RequestParam String surname,
                               @RequestParam String institution,
                               @RequestParam PositionType role) {
        memberService.editMember(id, name, surname, institution, role);
        return "redirect:/members";
    }

    @GetMapping("/delete/{id}")
    public String deleteMemberPage(Model model, @PathVariable Long id){
        Member member = memberService.findById(id);
        model.addAttribute("member", member);
        model.addAttribute("bodyContent", "delete-member");

        return "master-template";
    }

    @PostMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        Member member = memberService.deleteMember(id);

        return "redirect:/members";
    }

}
