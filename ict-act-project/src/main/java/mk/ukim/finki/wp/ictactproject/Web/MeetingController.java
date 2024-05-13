package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/meetings")
public class MeetingController {
    private final MeetingService meetingService;
    private final MemberService memberService;

    public MeetingController(MeetingService meetingService, MemberService memberService) {
        this.meetingService = meetingService;
        this.memberService = memberService;
    }

    @GetMapping
    private String listAllMeetings(Model model){
        model.addAttribute("meetings", meetingService.listAll());
        model.addAttribute("bodyContent", "all-meetings");

        return "master-template";
    }

    @GetMapping("/add")
    private String createMeetingForm(Model model){
        model.addAttribute("types", MeetingType.values());
        model.addAttribute("bodyContent", "create-new-meeting");
        return "master-template";
    }

    @PostMapping("/add")
    private String createMeeting(@RequestParam String topic,
                                 @RequestParam String room,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateAndTime,
                                 @RequestParam MeetingType type){

//        List<String> topics = Arrays.stream(discussionPoints.split(";")).toList();

        meetingService.create(topic, room, dateAndTime, type);
        return "redirect:/meetings";
    }

    @GetMapping("/details/{id}")
    private String getMeetingInfo(@PathVariable Long id, Model model) {
        Meeting meeting = meetingService.findMeetingById(id);
        model.addAttribute("meeting", meeting);
        model.addAttribute("bodyContent", "meeting-info");
        return "master-template";
    }

    @GetMapping("/in-progress/{id}")
    private String meetingInProgressPage(Model model, @PathVariable Long id) {
        Meeting meeting = meetingService.findMeetingById(id);
        Map<Long, List<Member>> membersVotedYes = meetingService.getMembersVotedYes(id);
        Map<Long, List<Member>> membersVotedNo = meetingService.getMembersVotedNo(id);
        Map<Long, List<Member>> members = meetingService.getAllMembersForMeeting(id, membersVotedYes, membersVotedNo);

        Map<Long, String> discussions = meetingService.getDiscussions(id);
        model.addAttribute("meeting", meeting);
        model.addAttribute("members", members);
        model.addAttribute("membersVotedYes", membersVotedYes);
        model.addAttribute("membersVotedNo", membersVotedNo);
        model.addAttribute("discussions", discussions);
        model.addAttribute("bodyContent", "meeting-in-progress");

        return "master-template";
    }

    @PostMapping("/finish/{id}")
    private String finishMeeting(Model model, @PathVariable Long id) {
        Meeting meeting = meetingService.finishMeeting(id);
        return "redirect:/meetings/details/" + id;
    }
}
