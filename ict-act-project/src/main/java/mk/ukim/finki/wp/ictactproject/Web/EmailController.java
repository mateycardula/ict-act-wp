package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Service.EmailService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/email")
public class EmailController {

    private final MeetingService meetingService;
    private final EmailService emailService;
    private final MemberService memberService;

    public EmailController(MeetingService meetingService, EmailService emailService, MemberService memberService) {
        this.meetingService = meetingService;
        this.emailService = emailService;
        this.memberService = memberService;
    }

    @GetMapping("/meeting-notification/{meetingId}")
    String meetingNotificationPage(Model model, @PathVariable long meetingId) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(meetingId);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        Map<String, String> placeholders = emailService.getMeetingNotificationPlaceholder(meeting);

        model.addAttribute("subject", placeholders.get("subject"));
        model.addAttribute("email_body", placeholders.get("body"));
        model.addAttribute("types", PositionType.excludeRoles(Collections.singletonList(PositionType.NEW_USER)));
        model.addAttribute("meetingId", meetingId);
        model.addAttribute("bodyContent", "meeting-notification");
        return "master-template";
    }

    @GetMapping("/send-notification/{meetingId}")
    String sendNotificationPage(Model model,
                                @RequestParam(required = false) String subject,
                                @RequestParam(required = false) String email_body,
                                @RequestParam(required = false) List<PositionType> recipientRoles,
                                @PathVariable long meetingId) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(meetingId);
        } catch (MeetingDoesNotExistException exception) {
            return "redirect:/meetings";
        }

        if(recipientRoles == null || recipientRoles.isEmpty()) {
            recipientRoles = PositionType.excludeRoles(Collections.singletonList(PositionType.NEW_USER));
        }

        if(subject != null && !subject.isEmpty()
            && email_body != null && !email_body.isEmpty()
        ) {
            emailService.saveMeetingDraft(subject, email_body, meeting);
        }

        model.addAttribute("subject", meeting.getDraftSubject());
        model.addAttribute("email_body", meeting.getDraftBody());
        model.addAttribute("recipientRoles", recipientRoles);
        model.addAttribute("meetingId", meetingId);
        model.addAttribute("bodyContent", "meeting-save-draft");

        return "master-template";

    }

    @PostMapping("/send-notification/{meetingId}")
    String sendNotification(@RequestParam(required = false) String subject,
                            @RequestParam(required = false) String email_body,
                            @RequestParam(required = false) List<PositionType> recipientRoles, @PathVariable long meetingId) {

        if(recipientRoles == null || recipientRoles.isEmpty()) {
            recipientRoles = PositionType.excludeRoles(Collections.singletonList(PositionType.NEW_USER));
        }

        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(meetingId);
        } catch (MeetingDoesNotExistException exception) {
            return "redirect:/meetings";
        }

        emailService.sendBatchMail(memberService.getEmailsByRole(recipientRoles), subject, email_body, meeting);
        return "redirect:/meetings/details/" + meetingId;
    }


    @GetMapping("/reset-draft/{meetingId}")
    String resetDraftConfirmationPage(Model model, @PathVariable long meetingId){
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(meetingId);
        } catch (MeetingDoesNotExistException exception) {
            return "redirect:/meetings";
        }
        model.addAttribute("bodyContent", "meeting-reset-draft");
        model.addAttribute("meeting", meeting);
        return "master-template";
    }

    @PostMapping("/reset-draft/{meetingId}")
    String resetDraft(@PathVariable long meetingId) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(meetingId);
        } catch (MeetingDoesNotExistException exception) {
            return "redirect:/meetings";
        }
        emailService.resetMeetingDraft(meeting);
        return "redirect:/email/meeting-notification/" + meetingId;
    }
}
