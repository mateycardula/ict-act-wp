package mk.ukim.finki.wp.ictactproject.Config;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInit {

    private final DiscussionPointsRepository discussionPointsRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final PasswordEncoder passwordEncoder;

    private final MeetingService meetingService;
    public DataInit(DiscussionPointsRepository discussionPointsRepository, MemberRepository memberRepository, MeetingRepository meetingRepository, PasswordEncoder passwordEncoder, MeetingService meetingService) {
        this.discussionPointsRepository = discussionPointsRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.passwordEncoder = passwordEncoder;
        this.meetingService = meetingService;
    }
    @PostConstruct
    void init(){
//        List<DiscussionPoint> dp1 = new ArrayList<>(),dp2 = new ArrayList<>();
//
        for(int i=0; i<5; i++){
            Member member = new Member();
            member.setName("Name" + i);
            member.setSurname("Surname" + i);
            member.setEmail("user" + i);
            member.setPassword(passwordEncoder.encode(
                    "p"
            ));
            member.setRole(PositionType.PRESIDENT);
            memberRepository.save(member);
        }

        for(int i=0; i<5; i++){
            Meeting meeting = new Meeting();
            meeting.setDateOfMeeting(LocalDateTime.now());
            meeting.setTopic("Topic" + i);
            meeting.setRoom("Room" + i);
            meetingRepository.save(meeting);
        }


//
//        LocalDateTime date = LocalDateTime.now();

//        meetingService.create("Meeting 1", "TMF 215", date, MeetingType.BOARD_MEETING, dp1);
//        meetingService.create("Meeting 2", "B3.2", date, MeetingType.GENERAL_ASSEMBLY, dp2);
    }
}
