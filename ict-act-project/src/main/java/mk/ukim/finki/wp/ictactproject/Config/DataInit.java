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
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Component
public class DataInit {

    private final DiscussionPointsRepository discussionPointsRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final PasswordEncoder passwordEncoder;
    private final DiscussionPointsService discussionPointsService;
    private final MeetingService meetingService;
  
    public DataInit(DiscussionPointsRepository discussionPointsRepository, MemberRepository memberRepository, MeetingRepository meetingRepository, PasswordEncoder passwordEncoder, DiscussionPointsService discussionPointsService, MeetingService meetingService) {
        this.discussionPointsRepository = discussionPointsRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.passwordEncoder = passwordEncoder;
        this.discussionPointsService = discussionPointsService;
        this.meetingService = meetingService;
    }
  
    @PostConstruct
    void init(){
        Member member = new Member();
        member.setName("Admin");
        member.setSurname("Admin");
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

            LocalDateTime date = LocalDateTime.now();
            LocalDateTime meetingDate = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), date.getHour(), date.getMinute());
            Meeting meeting = meetingService.create("topic" + i, "room" + i, meetingDate, MeetingType.BOARD_MEETING);
            DiscussionPoint discussionPoint = discussionPointsService.create("discussion point", "", true);
            meetingService.addDiscussionPoint(discussionPoint, meeting);
        }
    }
}
