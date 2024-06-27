package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Attachment;
import mk.ukim.finki.wp.ictactproject.Models.errors.DiscussionPointError;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.*;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

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
    public String createDiscussionPoint(Model model,
                                        @RequestParam Long meetingId,
                                        @RequestParam String topic,
                                        @RequestParam(required = false) boolean isVotable,
                                        @RequestParam("attachment") MultipartFile file,
                                        @RequestParam(required = false) String discussionText,
                                        RedirectAttributes redirectAttributes) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(meetingId);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        try {
            DiscussionPoint discussionPoint = discussionPointsService.create(topic, "", isVotable);

            if (!file.isEmpty()) {
                Attachment attachment = new Attachment(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                );
                discussionPoint.setAttachment(attachment);
                discussionPoint.setDiscussion(discussionText);
            }

            meetingService.addDiscussionPoint(discussionPoint, meeting);
            redirectAttributes.addFlashAttribute("message", "Discussion Point added successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to add Discussion Point: " + e.getMessage());
        }

        return "redirect:/meetings/details/"+meetingId;
    }

    @GetMapping("/edit/{id}")
    public String getEditPageForPoint(Model model, @PathVariable Long id) {
        DiscussionPoint discussionPoint;

        try {
            discussionPoint = discussionPointsService.getDiscussionPointById(id);
        }
        catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        model.addAttribute("bodyContent", "create-new-discussion-point");
        model.addAttribute("point", discussionPoint);
        model.addAttribute("meeting", discussionPointsService.getParentMeetingByDiscussionPointId(id));
        return "master-template";
    }

    @PostMapping("/edit/{id}")
    public String editDiscussionPoint(Model model,
                                      @PathVariable Long id,
                                      @RequestParam String topic,
                                      @RequestParam(required = false) boolean isVotable,
                                      @RequestParam("attachment") MultipartFile file,
                                      @RequestParam Long meetingId,
                                      @RequestParam(required = false) String discussionText,
                                      @RequestParam(required = false) boolean removeAttachment,
                                      RedirectAttributes redirectAttributes) {
        try {

            discussionPointsService.editDiscussionPoint(id, topic, discussionText, isVotable, file, removeAttachment);
            redirectAttributes.addFlashAttribute("message", "Discussion Point updated successfully!");

        } catch (DiscussionPointDoesNotExist exception) {

            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to update Discussion Point: " + e.getMessage());
        }

        if (removeAttachment)
            return "redirect:/discussion-point/edit/" + id;
        return "redirect:/meetings/details/" + meetingId;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable UUID id) {
        Attachment attachment = discussionPointsService.getAttachmentById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
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

//    abstained ---------------------------------------------------
    @PostMapping("/abstained/{discussionPointId}")
    public String abstainedForDiscussionPoint(Model model, @RequestParam(required = false) Long votes, @PathVariable Long discussionPointId, RedirectAttributes redirectAttributes) {
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
            discussionPoint = discussionPointsService.abstained(votes, discussionPointId);
        } catch (VotesMustBeZeroOrGreaterException | NumberOfVotesExceedsMembersAttendingException | NumberOfVotesExceedsRemainingMembers | DiscussionPointNotVotable exception) {
            redirectAttributes.addFlashAttribute("hasError", true);
            redirectAttributes.addFlashAttribute("error", new DiscussionPointError(discussionPointId, exception.getMessage(), "abstained"));
        }

        return "redirect:/meetings/panel/" + meeting.getId();
    }

    // ------------------------------------------------------------------------------

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

    // deti page for abstained --------------------------
    @GetMapping("/edit/votes/abstained/{id}")
    public String getEditPageForAbstained(Model model, @PathVariable Long id) {
        DiscussionPoint discussionPoint;

        try {
            discussionPoint = discussionPointsService.getDiscussionPointById(id);
        } catch (DiscussionPointDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        Long abstained = discussionPoint.getAbstained();

        model.addAttribute("point", discussionPoint);
        model.addAttribute("votes", abstained);
        model.addAttribute("bodyContent", "edit-abstained");

        if(model.asMap().get("hasError") != null) {
            model.addAttribute("hasError", (Boolean)model.asMap().get("hasError"));
            model.addAttribute("error", (String)model.asMap().get("error"));
        }

        return "master-template";
    }
    // -----------------------------------------------------------------------------------------------------


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

    // edit abstained -------------------------------------------------------------
    @PostMapping("/edit/votes/abstained/{id}")
    public String editAbstained(@PathVariable Long id, @RequestParam(required = false) Long votes, Model model, RedirectAttributes redirectAttributes) {
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
                DiscussionPoint discussionPoint = discussionPointsService.deleteAbstained(id);
            } catch (DiscussionPointDoesNotExist exception) {
                model.addAttribute("error", exception.getMessage());
                model.addAttribute("bodyContent", "error-404");
                return "master-template";
            }
        } else {
            try {
                DiscussionPoint discussionPoint = discussionPointsService.abstained(votes, id);
            } catch (VotesMustBeZeroOrGreaterException | NumberOfVotesExceedsMembersAttendingException | NumberOfVotesExceedsRemainingMembers exception) {
                redirectAttributes.addFlashAttribute("hasError", true);
                redirectAttributes.addFlashAttribute("error", exception.getMessage());
                return "redirect:/discussion-point/edit/votes/abstained/" + id;
            }
        }

        return "redirect:/meetings/panel/" + meeting.getId();
    }
    //-----------------------------------------------------------------------------------



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

        discussionPointsService.editDiscussion(id, discussionText);

        return "redirect:/meetings/panel/"+meeting.getId();
    }
}
