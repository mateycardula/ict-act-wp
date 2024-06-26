package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MeetingService {
    List<Meeting> listAll();

    Meeting create(String topic, String room, LocalDateTime dateAndTime, MeetingType meetingType);

    Meeting findMeetingById(Long id);

    Meeting addDiscussionPoint(DiscussionPoint discussionPoint, Meeting meeting);

    Meeting findMeetingByDiscussionPoint(Long discussionPointId);

    Map<Long, Long> getVotesYes(Long id);

    Map<Long, Long> getVotesNo(Long id);

    // abstained
    Map<Long, Long> getVotesAbstained(Long id);

    Map<Long, String> getDiscussions(Long id);

//    Map<Long, List<Member>> getAllMembersForMeeting(Long meetingId, Map<Long, List<Member>> membersVotedYes, Map<Long, List<Member>> membersVotedNo);

    Meeting finishMeeting(Long meetingId);

//    List<Member> getMembersForEditVotes(DiscussionPoint discussionPoint);

//    List<Member> getMembersThatVotedYes(Long id);

//    List<Member> getMembersThatVotedNo(Long id);

    List<Meeting> filter(String topic, LocalDateTime dateFrom, LocalDateTime dateTo, List<MeetingType> type);
    void deleteMeeting(Long id);
    Meeting editMeeting(Long id, String topic, String room, LocalDateTime dateAndTime, MeetingType type);
//    Meeting changeLoggedUserAttendanceStatus(Long meetingId);
    Meeting changeLoggedUserAttendanceStatus(Long meetingId, AttendanceStatus status);
    Meeting confirmUserAttendance(Member member, Meeting meeting);

    List<DiscussionPoint> getDiscussionPointsSorted(Long meetingId);
    List<Long> getMeetingsUserCheckedAttended();
    List<Long> getMeetingsUserMaybeCheckedAttended();
    List<Long> getMeetingsUserNotCheckedAttended();
    void addAttendants(List<Member> attendants, Long meetingId);
    void removeUserAttendance(Member member, Meeting meeting);
}
