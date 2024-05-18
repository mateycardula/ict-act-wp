package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @GetMapping("/all")
    public String allMembers(Model model){
        List<Member> allMembers = memberService.getAll();

        model.addAttribute("members", allMembers);
        model.addAttribute("bodyContent", "all-members");
        return "master-template";
    }

    @GetMapping("/edit/{id}")
    public String editMember(@PathVariable int id, Model model){
        Member member = memberService.findById(id);
        model.addAttribute("member", member);
        model.addAttribute("types", PositionType.values());
        model.addAttribute("bodyContent", "edit-member");
        return "master-template";
    }
}
