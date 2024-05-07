package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/discussion-point")
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
    public String createDiscussionPoint(Model model, @RequestParam Long meetingId, @RequestParam String topic) {
        Meeting meeting = meetingService.findMeetingById(meetingId);
        DiscussionPoint discussionPoint = discussionPointsService.create(topic, "");

        meetingService.addDiscussionPoint(discussionPoint, meeting);

        return "redirect:/meetings";
    }

    @PostMapping("/vote/yes/{discussionPointId}")
    public String voteYesForDiscussionPoint(Model model, @RequestParam Long[] memberIds, @PathVariable Long discussionPointId) {
        DiscussionPoint discussionPoint = discussionPointsService.voteYes(memberIds, discussionPointId);
        Meeting meeting = meetingService.findMeetingByDiscussionPoint(discussionPointId);
        return "redirect:/meetings/in-progress/" + meeting.getId();
    }

    @PostMapping("/vote/no/{discussionPointId}")
    public String voteNoForDiscussionPoint(Model model, @RequestParam Long[] memberIds, @PathVariable Long discussionPointId) {
        DiscussionPoint discussionPoint = discussionPointsService.voteNo(memberIds, discussionPointId);
        Meeting meeting = meetingService.findMeetingByDiscussionPoint(discussionPointId);
        return "redirect:/meetings/in-progress/" + meeting.getId();
    }

    @PostMapping("/add/discussion/{discussionPointId}")
    public String addDiscussionToDiscussionPoint(Model model, @RequestParam String discussion, @PathVariable Long discussionPointId) {
        DiscussionPoint discussionPoint = discussionPointsService.addDiscussion(discussion, discussionPointId);
        Meeting meeting = meetingService.findMeetingByDiscussionPoint(discussionPointId);
        return "redirect:/meetings/in-progress/" + meeting.getId();
    }
}
