package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    List<Meeting> listAll();
    Meeting create(String topic, String room, LocalDateTime dateAndTime, MeetingType meetingType, List<String> discussionPoints);
}
