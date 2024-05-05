package mk.ukim.finki.wp.ictactproject.Config;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInit {

    private final DiscussionPointsRepository discussionPointsRepository;
    private final MeetingService meetingService;
    public DataInit(DiscussionPointsRepository discussionPointsRepository, MeetingService meetingService) {
        this.discussionPointsRepository = discussionPointsRepository;

        this.meetingService = meetingService;
    }
    @PostConstruct
    void init(){
//        List<DiscussionPoint> dp1 = new ArrayList<>(),dp2 = new ArrayList<>();
//
//        for(int i=0; i<5; i++){
//            DiscussionPoint discussionPoint = new DiscussionPoint();
//            discussionPoint.setTopic("Topic " + i);
//            discussionPointsRepository.save(discussionPoint);
//
//            if(i<=2) dp1.add(discussionPoint);
//            else dp2.add(discussionPoint);
//        }
//
//        LocalDateTime date = LocalDateTime.now();

//        meetingService.create("Meeting 1", "TMF 215", date, MeetingType.BOARD_MEETING, dp1);
//        meetingService.create("Meeting 2", "B3.2", date, MeetingType.GENERAL_ASSEMBLY, dp2);
    }
}
