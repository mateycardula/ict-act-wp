package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Models.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MeetingService {
    List<Meeting> listAll();
    Meeting create(String topic, String room, LocalDateTime dateAndTime, MeetingType meetingType);
    Meeting findMeetingById(Long id);
    Meeting addDiscussionPoint(DiscussionPoint discussionPoint, Meeting meeting);
    Meeting findMeetingByDiscussionPoint(Long discussionPointId);
    Map<Long,List<Member>> getMembersVotedYes(Long id);
    Map<Long,List<Member>> getMembersVotedNo(Long id);
    Map<Long, String> getDiscussions(Long id);
    Map<Long, List<Member>> getAllMembersForMeeting(Long meetingId, Map<Long, List<Member>> membersVotedYes, Map<Long, List<Member>> membersVotedNo);
}
