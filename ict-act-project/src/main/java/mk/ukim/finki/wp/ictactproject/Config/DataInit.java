package mk.ukim.finki.wp.ictactproject.Config;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.stereotype.Component;

@Component
public class DataInit {

    private final DiscussionPointsRepository discussionPointsRepository;
    private final MemberRepository memberRepository;
    private final MeetingService meetingService;
    public DataInit(DiscussionPointsRepository discussionPointsRepository, MemberRepository memberRepository, MeetingService meetingService) {
        this.discussionPointsRepository = discussionPointsRepository;
        this.memberRepository = memberRepository;

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
            memberRepository.save(member);
        }
//
//        LocalDateTime date = LocalDateTime.now();

//        meetingService.create("Meeting 1", "TMF 215", date, MeetingType.BOARD_MEETING, dp1);
//        meetingService.create("Meeting 2", "B3.2", date, MeetingType.GENERAL_ASSEMBLY, dp2);
    }
}
