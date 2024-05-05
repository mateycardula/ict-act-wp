package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingService {
    List<Meeting> listAll();
    Meeting create(String topic, String room, LocalDateTime dateAndTime, MeetingType meetingType);
    Meeting findMeetingById(Long id);
    Meeting addDiscussionPoint(DiscussionPoint discussionPoint, Meeting meeting);
}
