package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.errors.DiscussionPointError;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MemberDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/meetings")
public class MeetingController {
    private final MeetingService meetingService;
    private final MemberService memberService;
    private final DiscussionPointsService discussionPointsService;

    public MeetingController(MeetingService meetingService, MemberService memberService, DiscussionPointsService discussionPointsService) {
        this.meetingService = meetingService;
        this.memberService = memberService;
        this.discussionPointsService = discussionPointsService;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Long> meetingsAttended = new ArrayList<>();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            meetingsAttended = meetingService.getMeetingsUserCheckedAttended(username);
        }
        model.addAttribute("attended", meetingsAttended);


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

        model.addAttribute("meeting", meeting);
        model.addAttribute("bodyContent", "meeting-info");
        return "master-template";
    }

    @GetMapping("/in-progress/{id}")
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
        Map<Long, String> discussions = meetingService.getDiscussions(id);

        model.addAttribute("meeting", meeting);
        model.addAttribute("votesYes", membersVotedYes);
        model.addAttribute("votesNo", membersVotedNo);
        model.addAttribute("discussions", discussions);
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
        // TODO: CHECK IF THE LOGGED IN USER IS THE PRESIDENT/VICE PRESIDENT
        Meeting meeting;
        try {
            meeting = meetingService.findMeetingById(id);
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
        Meeting meeting;
        try {
            meeting = meetingService.editMeeting(id, topic, room, dateAndTime, type);
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

    @PostMapping("/attend/{id}")
    public String attendMeeting(Model model, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        } else {
            return "redirect:/login";
        }

        try {
            Meeting meeting = meetingService.userAttendMeeting(username, id);
        } catch (MeetingDoesNotExistException | MemberDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        return "redirect:/meetings";
    }

    @PostMapping("/cancel-attendance/{id}")
    public String cancelAttendMeeting(Model model, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        } else {
            return "redirect:/login";
        }

        try {
            Meeting meeting = meetingService.userCancelAttendance(username, id);
        } catch (MeetingDoesNotExistException | MemberDoesNotExist exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "error-404");
            return "master-template";
        }

        return "redirect:/meetings";
    }
}
