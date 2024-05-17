package mk.ukim.finki.wp.ictactproject.Config;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInit {

    private final DiscussionPointsRepository discussionPointsRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingService meetingService;
    public DataInit(DiscussionPointsRepository discussionPointsRepository, MemberRepository memberRepository, MeetingRepository meetingRepository, MeetingService meetingService) {
        this.discussionPointsRepository = discussionPointsRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;

        this.meetingService = meetingService;
    }
    @PostConstruct
    void init(){
        for(int i=0; i<5; i++){
            Member member = new Member();
            member.setName("Name" + i);
            member.setSurname("Surname" + i);
            memberRepository.save(member);

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
