package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;
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

        return "redirect:/discussion-point/add?meetingId="+meetingId;
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

    @GetMapping("/edit/votes/yes/{id}")
    public String getEditPageForVotingYes(Model model, @PathVariable Long id) {
        DiscussionPoint discussionPoint = discussionPointsService.getDiscussionPointById(id);
        List<Member> membersToShow = meetingService.getMembersForEditVotes(discussionPoint);
        List<Member> membersToCheck = meetingService.getMembersThatVotedYes(id);

        model.addAttribute("point", discussionPoint);
        model.addAttribute("members", membersToShow);
        model.addAttribute("toCheck", membersToCheck);
        model.addAttribute("bodyContent", "edit-votes-yes");

        return "master-template";
    }

    @GetMapping("/edit/votes/no/{id}")
    public String getEditPageForVotingNo(Model model, @PathVariable Long id) {
        DiscussionPoint discussionPoint = discussionPointsService.getDiscussionPointById(id);
        List<Member> membersToShow = meetingService.getMembersForEditVotes(discussionPoint);
        List<Member> membersToCheck = meetingService.getMembersThatVotedNo(id);

        model.addAttribute("point", discussionPoint);
        model.addAttribute("members", membersToShow);
        model.addAttribute("toCheck", membersToCheck);
        model.addAttribute("bodyContent", "edit-votes-no");

        return "master-template";
    }

    @PostMapping("/edit/votes/yes/{id}")
    public String editVotesYes(@PathVariable Long id, @RequestParam(required = false) Long[] memberIds) {
        if(memberIds == null) {
            DiscussionPoint discussionPoint = discussionPointsService.deleteVotesYes(id);
        } else {
            DiscussionPoint discussionPoint = discussionPointsService.editVoteYes(memberIds, id);
        }
        Meeting meeting = meetingService.findMeetingByDiscussionPoint(id);
        return "redirect:/meetings/in-progress/" + meeting.getId();
    }

    @PostMapping("/edit/votes/no/{id}")
    public String editVotesNo(@PathVariable Long id, @RequestParam(required = false) Long[] memberIds) {
        if(memberIds == null) {
            DiscussionPoint discussionPoint = discussionPointsService.deleteVotesNo(id);
        } else {
            DiscussionPoint discussionPoint = discussionPointsService.editVoteNo(memberIds, id);
        }
        Meeting meeting = meetingService.findMeetingByDiscussionPoint(id);
        return "redirect:/meetings/in-progress/" + meeting.getId();
    }

    @GetMapping("/edit/discussion/{id}")
    public String getEditPageForDiscussion(Model model, @PathVariable Long id) {
        DiscussionPoint discussionPoint = discussionPointsService.getDiscussionPointById(id);
        String discussionText = discussionPoint.getDiscussion();

        model.addAttribute("discussionPoint", discussionPoint);
        model.addAttribute("discussionText", discussionText);
        model.addAttribute("bodyContent", "edit-discussion");

        return "master-template";
    }

    @PostMapping("/edit/discussion/{id}")
    public String editDiscussion(Model model, @PathVariable Long id,
                                 @RequestParam(required = false) String discussionText){
        Meeting meeting = meetingService.findMeetingByDiscussionPoint(id);

        discussionPointsService.editDiscussion(meeting, id, discussionText);

        return "redirect:/meetings/details/"+meeting.getId();
    }
}
