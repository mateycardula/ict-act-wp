package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/discussion")
public class DiscussionPointController {
    private final DiscussionPointsService discussionPointsService;
    private final MeetingService meetingService;


    public DiscussionPointController(DiscussionPointsService discussionPointsService, MeetingService meetingService) {
        this.discussionPointsService = discussionPointsService;
        this.meetingService = meetingService;
    }

    @GetMapping("/add")
    public String getCreateDiscussionPointPage(Model model, @RequestParam Long meetingId) {
        Meeting meeting = meetingService.findMeetingById(meetingId);
        model.addAttribute("bodyContent", "create-new-discussion-point");
        model.addAttribute("meeting", meeting);
        return "master-template";
    }

    @PostMapping("/add")
    public String createDiscussionPoint(Model model, @RequestParam Long meetingId, @RequestParam String topic, @RequestParam String discussion) {
        Meeting meeting = meetingService.findMeetingById(meetingId);
        DiscussionPoint discussionPoint = discussionPointsService.create(topic, discussion);

        meetingService.addDiscussionPoint(discussionPoint, meeting);

        return "redirect:/meetings";
    }
}
