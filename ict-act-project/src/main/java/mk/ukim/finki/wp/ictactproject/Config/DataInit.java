package mk.ukim.finki.wp.ictactproject.Config;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.ictactproject.Models.*;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Repository.PositionRepository;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    private final PositionRepository positionRepository;
  
    public DataInit(DiscussionPointsRepository discussionPointsRepository, MemberRepository memberRepository, MeetingRepository meetingRepository, PasswordEncoder passwordEncoder, DiscussionPointsService discussionPointsService, MeetingService meetingService, PositionRepository positionRepository) {
        this.discussionPointsRepository = discussionPointsRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
        this.passwordEncoder = passwordEncoder;
        this.discussionPointsService = discussionPointsService;
        this.meetingService = meetingService;
        this.positionRepository = positionRepository;
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
        Position newPosition = new Position(PositionType.PRESIDENT, LocalDate.now());
        positionRepository.save(newPosition);
        System.out.println(member.getAuthorities());
        member.setPositions(Collections.singletonList(newPosition));
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
