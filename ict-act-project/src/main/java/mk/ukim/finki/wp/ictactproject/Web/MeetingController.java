package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.*;
import mk.ukim.finki.wp.ictactproject.Models.errors.DiscussionPointError;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import mk.ukim.finki.wp.ictactproject.Service.EmailService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/meetings")
public class MeetingController {
    private final MeetingService meetingService;
    private final MemberService memberService;
    private final DiscussionPointsService discussionPointsService;
    private final EmailService emailService;

    public MeetingController(MeetingService meetingService, MemberService memberService, DiscussionPointsService discussionPointsService, EmailService emailService) {
        this.meetingService = meetingService;
        this.memberService = memberService;
        this.discussionPointsService = discussionPointsService;
        this.emailService = emailService;
    }

    @GetMapping
    private String listAllMeetings(Model model,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                   @RequestParam(required = false) List<MeetingType> type) {
        LocalDateTime dateTimeFrom = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime dateTimeTo = dateTo != null ? dateTo.plusDays(1).atStartOfDay().minusNanos(1) : null;

        model.addAttribute("meetings", meetingService.filter(name, dateTimeFrom, dateTimeTo, type));
        model.addAttribute("types", MeetingType.values());
        model.addAttribute("bodyContent", "all-meetings");
        model.addAttribute("attended_meetings", meetingService.getMeetingsUserCheckedAttended());
        model.addAttribute("maybe_attended_meetings", meetingService.getMeetingsUserMaybeCheckedAttended());
        model.addAttribute("not_attended_meetings", meetingService.getMeetingsUserNotCheckedAttended());
//        emailService.sendEmail("ana.kostadinovska6@gmail.com", "YOU ACCESSED THE MEETING PAGE", "IT WORKS!");
        return "master-template";
    }

    @GetMapping("/add")
    private String createMeetingForm(Model model) {
        model.addAttribute("types", MeetingType.values());
        model.addAttribute("bodyContent", "create-new-meeting");
        return "master-template";
    }

    @PostMapping("/add")
    private String createMeeting(@RequestParam String topic,
                                 @RequestParam String room,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateAndTime,
                                 @RequestParam MeetingType type) {

//        List<String> topics = Arrays.stream(discussionPoints.split(";")).toList();

        meetingService.create(topic, room, dateAndTime, type);
        return "redirect:/meetings";
    }

    @GetMapping("/details/{id}")
    private String getMeetingInfo(@PathVariable Long id, Model model) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(id);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        List<DiscussionPoint> sortedDiscussions = meetingService.getDiscussionPointsSorted(meeting.getId());
        model.addAttribute("meeting", meeting);
        model.addAttribute("sortedDiscussions", sortedDiscussions);
        model.addAttribute("bodyContent", "meeting-info");
        model.addAttribute("attended_meetings", meetingService.getMeetingsUserCheckedAttended());

        return "master-template";
    }

    @GetMapping("/panel/{id}")
    private String meetingInProgressPage(Model model, @PathVariable Long id) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(id);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        Map<Long, Long> membersVotedYes = meetingService.getVotesYes(id);
        Map<Long, Long> membersVotedNo = meetingService.getVotesNo(id);
//        abstained
        Map<Long, Long> membersAbstained = meetingService.getVotesAbstained(id);

        Map<Long, String> discussions = meetingService.getDiscussions(id);
        List<DiscussionPoint> sortedDiscussions = meetingService.getDiscussionPointsSorted(id);
        model.addAttribute("meeting", meeting);
        model.addAttribute("votesYes", membersVotedYes);
        model.addAttribute("votesNo", membersVotedNo);
        model.addAttribute("abstained", membersAbstained);
        model.addAttribute("discussions", discussions);
        model.addAttribute("sortedDiscussions", sortedDiscussions);
        model.addAttribute("bodyContent", "meeting-in-progress");

        if(model.asMap().get("hasError") != null) {
            model.addAttribute("hasError", (Boolean)model.asMap().get("hasError"));
            model.addAttribute("error", (DiscussionPointError)model.asMap().get("error"));
        }

        return "master-template";
    }

    @PostMapping("/finish/{id}")
    private String finishMeeting(Model model, @PathVariable Long id) {
        Meeting meeting;
        try {
            meeting = meetingService.finishMeeting(id);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }
        return "redirect:/meetings/details/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteMeeting(Model model, @PathVariable Long id) {
        try {
            meetingService.findMeetingById(id);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        meetingService.deleteMeeting(id);
        return "redirect:/meetings";
    }

    @GetMapping("/edit/{id}")
    public String getEditPage(Model model, @PathVariable Long id) {
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(id);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        model.addAttribute("meeting", meeting);
        model.addAttribute("types", MeetingType.values());
        model.addAttribute("bodyContent", "edit-meeting");
        return "master-template";
    }

    @PostMapping("/edit/{id}")
    public String editMeeting(Model model,
                              @PathVariable Long id,
                              @RequestParam String topic,
                              @RequestParam String room,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateAndTime,
                              @RequestParam MeetingType type) {
        try {
            meetingService.editMeeting(id, topic, room, dateAndTime, type);
        } catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }
        return "redirect:/meetings";
    }

    @PostMapping("/delete/discussion/{id}")
    public String deleteDiscussion(Model model, @PathVariable Long id) {

        Meeting meeting = meetingService.findMeetingByDiscussionPoint(id);
        discussionPointsService.deleteDiscussion(meeting, id);

        return "redirect:/meetings/details/"+meeting.getId();
    }

   @PostMapping("/change-attendance/{id}")
   public String changeMeetingsAttendanceStatus(Model model, @PathVariable Long id, @RequestParam String status) {
       AttendanceStatus attendanceStatus;

       if (status.isEmpty()) {
           attendanceStatus = AttendanceStatus.NONE;
       } else {
           attendanceStatus = AttendanceStatus.valueOf(status);
       }

       try {
           Meeting meeting = meetingService.changeLoggedUserAttendanceStatus(id, attendanceStatus);
       } catch (MeetingDoesNotExistException exception) {
           model.addAttribute("error", exception.getMessage());
           model.addAttribute("bodyContent", "error-404");
           return "master-template";
       }

       return "redirect:/meetings";
   }



    @GetMapping("/add-attendees/{id}")
    public String addAttendantsForm(Model model, @PathVariable Long id) {
        Meeting meeting;

        try{
            meeting = meetingService.findMeetingById(id);
        }
        catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        model.addAttribute("meeting", meeting);


        Set<Member> registeredAttendees = new HashSet<>(meeting.getRegisteredAttendees());
        Set<Member> confirmedAttendees = new HashSet<>(meeting.getAttendees());
        Set<Member> otherMembers = new HashSet<>(memberService.getAll());
        Set<Member> maybeAttendees = new HashSet<>(meeting.getMaybeAttendees());

        registeredAttendees.removeAll(confirmedAttendees);
        maybeAttendees.removeAll(confirmedAttendees);

        otherMembers.removeAll(registeredAttendees);
        otherMembers.removeAll(confirmedAttendees);
        otherMembers.removeAll(maybeAttendees);


        model.addAttribute("confirmedAttendees", confirmedAttendees.stream().toList());
        model.addAttribute("registeredMembers", registeredAttendees.stream().toList());
        model.addAttribute("maybeMembers", maybeAttendees.stream().toList());
        model.addAttribute("members", otherMembers.stream().toList());
        model.addAttribute("bodyContent", "add-attendees");

        return "master-template";
    }

    @PostMapping("/add-attendees")
    public String addAttendants(@RequestParam Long meetingId, @RequestParam(required = false) List<Long> attendants){
        System.out.println(attendants);
        List<Member> attendantsToAdd = memberService.getMultipleByIds(attendants);
        meetingService.addAttendants(attendantsToAdd, meetingId);
        return "redirect:/meetings/panel/" + meetingId;
    }

    @GetMapping("/registered-members/{id}")
    public String registeredMembers(Model model, @PathVariable Long id, @RequestParam(defaultValue = "yes") String tab) {
        Meeting meeting;

        try{
            meeting = meetingService.findMeetingById(id);
        }
        catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        model.addAttribute("meetingId", meeting.getId());
//        model.addAttribute("members", meeting.getRegisteredAttendees());
        model.addAttribute("yesMembers", meeting.getRegisteredAttendees());
        model.addAttribute("maybeMembers", meeting.getMaybeAttendees());
        model.addAttribute("noMembers", meeting.getNoAttendees());
        model.addAttribute("defaultTab", tab);
        model.addAttribute("bodyContent", "meeting-registered-attendees");
        return "master-template";

    }

    @GetMapping("/meeting-attendants/{id}")
    public String meetingAttendants(Model model, @PathVariable Long id) {
        Meeting meeting;

        try{
            meeting = meetingService.findMeetingById(id);
        }
        catch (MeetingDoesNotExistException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        model.addAttribute("meetingId", meeting.getId());
        model.addAttribute("members", meeting.getAttendees());
        model.addAttribute("bodyContent", "meeting-registered-attendees");
        return "master-template";
    }

}
