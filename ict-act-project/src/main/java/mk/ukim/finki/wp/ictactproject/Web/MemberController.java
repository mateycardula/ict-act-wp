package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import mk.ukim.finki.wp.ictactproject.Service.PositionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final PositionService positionService;

    public MemberController(MemberService memberService, PositionService positionService) {
        this.memberService = memberService;
        this.positionService = positionService;
    }

    @GetMapping()
    public String getAllMembers(Model model) {
        List<Member> members = memberService.getAll();
        model.addAttribute("members", members);
        model.addAttribute("bodyContent", "all-members");

        return "master-template";
    }

    @GetMapping("/edit/{id}")
    public String getEditMemberPage(Model model, @PathVariable Long id) {
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

        Member member = memberService.findById(id);


        memberService.editMember(id, name, surname, institution, role);
        return "redirect:/members";
    }

    @GetMapping("/delete/{id}")
    public String deleteMemberPage(Model model, @PathVariable Long id) {
        Member member = memberService.findById(id);
        model.addAttribute("member", member);
        model.addAttribute("bodyContent", "delete-member");

        return "master-template";
    }

    @PostMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);

        return "redirect:/members";
    }

    @GetMapping("positions/{id}")
    public String getPositionsPage(Model model, @PathVariable Long id) {
        model.addAttribute("positions", memberService.getPositionsByMember(id));
        model.addAttribute("member", memberService.findById(id));
        model.addAttribute("bodyContent", "all-positions");

        return "master-template";
    }

    @GetMapping("/{member_id}/positions/add")
    public String getAddPositionPage(@PathVariable Long member_id, Model model) {
        model.addAttribute("types", PositionType.values());
        model.addAttribute("member", memberService.findById(member_id));
        model.addAttribute("bodyContent", "edit-positions");

        return "master-template";
    }

    @PostMapping("{member_id}/positions/add")
    public String addPosition(@PathVariable Long member_id,
                              @RequestParam PositionType role,
                              @RequestParam LocalDate dateFrom,
                              @RequestParam(required = false) LocalDate dateTo) {
        this.memberService.addPosition(member_id, role, dateFrom, dateTo);

        return "redirect:/members/positions/" + member_id;
    }


    @GetMapping("/{member_id}/positions/{position_id}/edit")
    public String getEditPositionPage(@PathVariable Long member_id, @PathVariable Long position_id, Model model) {
        model.addAttribute("types", PositionType.values());
        model.addAttribute("member", memberService.findById(member_id));
        model.addAttribute("position", positionService.findById(position_id));
        model.addAttribute("bodyContent", "edit-positions");

        return "master-template";
    }

    @PostMapping("/{member_id}/positions/{position_id}/edit")
    public String editPosition(@PathVariable Long member_id,
                               @PathVariable Long position_id,
                               @RequestParam PositionType role,
                               @RequestParam LocalDate dateFrom,
                               @RequestParam(required = false) LocalDate dateTo) {
        this.memberService.editPosition(member_id, position_id, role, dateFrom, dateTo);

        return "redirect:/members/positions/" + member_id;
    }

    @GetMapping("/{member_id}/positions/{position_id}/delete")
    public String getDeletePositionPage(@PathVariable Long member_id, @PathVariable Long position_id, Model model) {
        model.addAttribute("member", memberService.findById(member_id));
        model.addAttribute("position", positionService.findById(position_id));
        model.addAttribute("bodyContent", "delete-position");

        return "master-template";
    }

    @PostMapping("/{member_id}/positions/{position_id}/delete")
    public String DeletePosition(@PathVariable Long member_id, @PathVariable Long position_id) {
        this.memberService.deletePosition(member_id, position_id);

        return "redirect:/members/positions/" + member_id;
    }
}
