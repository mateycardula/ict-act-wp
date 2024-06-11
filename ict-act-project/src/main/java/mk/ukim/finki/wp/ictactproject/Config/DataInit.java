package mk.ukim.finki.wp.ictactproject.Config;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
        Member member = new Member();
        member.setName("Name");
        member.setSurname("Surname");
        member.setEmail("admin");
        member.setPassword(passwordEncoder.encode(
                "admin"
        ));

        member.setRole(PositionType.PRESIDENT);
        System.out.println(member.getAuthorities());
        memberRepository.save(member);

       member = new Member();
        member.setName("Name");
        member.setSurname("Surname");
        member.setEmail("new@user");
        member.setPassword(passwordEncoder.encode(
                "user"
        ));
        member.setRole(PositionType.NEW_USER);
        memberRepository.save(member);

        for(int i=0; i<5; i++){

            Meeting meeting = new Meeting();
            LocalDateTime date = LocalDateTime.now();
            LocalDateTime finishedDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), date.getHour(), date.getMinute());
            meeting.setTopic("topic" + i);
            meeting.setRoom("room" + i);
            meeting.setMeetingType(MeetingType.BOARD_MEETING);
            meeting.setDateOfMeeting(finishedDate);
            List<DiscussionPoint> discussionPointList = new ArrayList<>();
            for(int j=0; j<3; j++) {
                DiscussionPoint discussionPoint = new DiscussionPoint();
                discussionPoint.setTopic("discussion" + i + j);
                discussionPointList.add(discussionPoint);
                discussionPointsRepository.save(discussionPoint);
            }
            meeting.setDiscussionPoints(discussionPointList);
            meetingRepository.save(meeting);
        }
    }
}
