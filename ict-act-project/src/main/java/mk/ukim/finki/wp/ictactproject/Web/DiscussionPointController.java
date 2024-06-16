package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.errors.DiscussionPointError;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.*;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(meetingId);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }
        model.addAttribute("bodyContent", "create-new-discussion-point");
        model.addAttribute("meeting", meeting);
        return "master-template";
    }

    @PostMapping("/add")
    public String createDiscussionPoint(Model model, @RequestParam Long meetingId, @RequestParam String topic) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(meetingId);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        DiscussionPoint discussionPoint = discussionPointsService.create(topic, "", false);

        meetingService.addDiscussionPoint(discussionPoint, meeting);

        return "redirect:/meetings/details/"+meetingId;
    }

    @PostMapping("/vote/yes/{discussionPointId}")
    public String voteYesForDiscussionPoint(Model model, @RequestParam(required = false) Long votes, @PathVariable Long discussionPointId, RedirectAttributes redirectAttributes) {
        DiscussionPoint discussionPoint;
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingByDiscussionPoint(discussionPointId);
        } catch (DiscussionPointDoesNotExist | DiscussionPointNotVotable exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        try {
            discussionPoint = discussionPointsService.voteYes(votes, discussionPointId);
        } catch (VotesMustBeZeroOrGreaterException | NumberOfVotesExceedsMembersAttendingException | NumberOfVotesExceedsRemainingMembers | DiscussionPointNotVotable exception) {
            redirectAttributes.addFlashAttribute("hasError", true);
            redirectAttributes.addFlashAttribute("error", new DiscussionPointError(discussionPointId, exception.getMessage(), "yes"));
        }

        return "redirect:/meetings/panel/" + meeting.getId();
    }

    @PostMapping("/vote/no/{discussionPointId}")
    public String voteNoForDiscussionPoint(Model model, @RequestParam(required = false) Long votes, @PathVariable Long discussionPointId, RedirectAttributes redirectAttributes) {
        DiscussionPoint discussionPoint;
        Meeting meeting;

        try {
            meeting = meetingService.findMeetingByDiscussionPoint(discussionPointId);
        } catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        try {
            discussionPoint = discussionPointsService.voteNo(votes, discussionPointId);
        } catch (VotesMustBeZeroOrGreaterException | NumberOfVotesExceedsMembersAttendingException | NumberOfVotesExceedsRemainingMembers | DiscussionPointNotVotable  exception) {
            redirectAttributes.addFlashAttribute("hasError", true);
            redirectAttributes.addFlashAttribute("error", new DiscussionPointError(discussionPointId, exception.getMessage(), "no"));
        }

        return "redirect:/meetings/panel/" + meeting.getId();
    }

    @PostMapping("/add/discussion/{discussionPointId}")
    public String addDiscussionToDiscussionPoint(Model model, @RequestParam String discussion, @PathVariable Long discussionPointId) {
        Meeting meeting;
        DiscussionPoint discussionPoint;

        try {
            discussionPoint = discussionPointsService.addDiscussion(discussion, discussionPointId);
            meeting = meetingService.findMeetingByDiscussionPoint(discussionPointId);
        } catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        return "redirect:/meetings/panel/" + meeting.getId();
    }

    @GetMapping("/edit/votes/yes/{id}")
    public String getEditPageForVotingYes(Model model, @PathVariable Long id) {
        DiscussionPoint discussionPoint;

        try {
            discussionPoint = discussionPointsService.getDiscussionPointById(id);
        } catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        Long votesYes = discussionPoint.getVotesYes();

        model.addAttribute("point", discussionPoint);
        model.addAttribute("votes", votesYes);
        model.addAttribute("bodyContent", "edit-votes-yes");

        if(model.asMap().get("hasError") != null) {
            model.addAttribute("hasError", (Boolean)model.asMap().get("hasError"));
            model.addAttribute("error", (String)model.asMap().get("error"));
        }

        return "master-template";
    }

    @GetMapping("/edit/votes/no/{id}")
    public String getEditPageForVotingNo(Model model, @PathVariable Long id) {
        DiscussionPoint discussionPoint;

        try {
            discussionPoint = discussionPointsService.getDiscussionPointById(id);
        } catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        Long votesNo = discussionPoint.getVotesNo();

        model.addAttribute("point", discussionPoint);
        model.addAttribute("votes", votesNo);
        model.addAttribute("bodyContent", "edit-votes-no");

        if(model.asMap().get("hasError") != null) {
            model.addAttribute("hasError", (Boolean)model.asMap().get("hasError"));
            model.addAttribute("error", (String)model.asMap().get("error"));
        }

        return "master-template";
    }

    @PostMapping("/edit/votes/yes/{id}")
    public String editVotesYes(@PathVariable Long id, @RequestParam(required = false) Long votes, Model model, RedirectAttributes redirectAttributes) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingByDiscussionPoint(id);
        } catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        if(votes == null) {
            try {
                DiscussionPoint discussionPoint = discussionPointsService.deleteVotesYes(id);
            } catch (DiscussionPointDoesNotExist exception) {
                model.addAttribute("error", exception.getMessage());
                model.addAttribute("bodyContent", "error-404");
                return "master-template";
            }
        } else {
            try {
                DiscussionPoint discussionPoint = discussionPointsService.voteYes(votes, id);
            } catch (VotesMustBeZeroOrGreaterException | NumberOfVotesExceedsMembersAttendingException | NumberOfVotesExceedsRemainingMembers exception) {
                redirectAttributes.addFlashAttribute("hasError", true);
                redirectAttributes.addFlashAttribute("error", exception.getMessage());
                return "redirect:/discussion-point/edit/votes/yes/" + id;
            }
        }

        return "redirect:/meetings/panel/" + meeting.getId();
    }

    @PostMapping("/edit/votes/no/{id}")
    public String editVotesNo(@PathVariable Long id, @RequestParam(required = false) Long votes, Model model, RedirectAttributes redirectAttributes) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingByDiscussionPoint(id);
        } catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        if(votes == null) {
            try {
                DiscussionPoint discussionPoint = discussionPointsService.deleteVotesNo(id);
            } catch (DiscussionPointDoesNotExist exception) {
                model.addAttribute("error", exception.getMessage());
                model.addAttribute("bodyContent", "error-404");
                return "master-template";
            }
        } else {
            try {
                DiscussionPoint discussionPoint = discussionPointsService.voteNo(votes, id);
            } catch (VotesMustBeZeroOrGreaterException | NumberOfVotesExceedsMembersAttendingException | NumberOfVotesExceedsRemainingMembers exception) {
                redirectAttributes.addFlashAttribute("hasError", true);
                redirectAttributes.addFlashAttribute("error", exception.getMessage());
                return "redirect:/discussion-point/edit/votes/no/" + id;
            }
        }

        return "redirect:/meetings/panel/" + meeting.getId();
    }

    @GetMapping("/edit/discussion/{id}")
    public String getEditPageForDiscussion(Model model, @PathVariable Long id) {
        DiscussionPoint discussionPoint;
        try {
            discussionPoint = discussionPointsService.getDiscussionPointById(id);
        } catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }
        String discussionText = discussionPoint.getDiscussion();

        model.addAttribute("discussionPoint", discussionPoint);
        model.addAttribute("discussionText", discussionText);
        model.addAttribute("bodyContent", "edit-discussion");

        return "master-template";
    }

    @PostMapping("/edit/discussion/{id}")
    public String editDiscussion(Model model, @PathVariable Long id,
                                 @RequestParam(required = false) String discussionText){
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingByDiscussionPoint(id);
        } catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        discussionPointsService.editDiscussion(meeting, id, discussionText);

        return "redirect:/meetings/panel/"+meeting.getId();
    }
}
