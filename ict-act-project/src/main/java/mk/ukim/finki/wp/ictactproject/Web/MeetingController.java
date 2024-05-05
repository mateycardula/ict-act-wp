package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/meetings")
public class MeetingController {
    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
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
}
