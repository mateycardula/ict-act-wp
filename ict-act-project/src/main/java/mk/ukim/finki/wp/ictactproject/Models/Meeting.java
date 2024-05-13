package mk.ukim.finki.wp.ictactproject.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //naslovot na meeting-ot
    private String topic;

    private String room;

    private LocalDateTime dateOfMeeting;

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @OneToMany
    private List<DiscussionPoint> discussionPoints;

    @OneToOne
    private MeetingReport meetingReport;

    private boolean finished = false;
    public Meeting() {}

    public Meeting(String topic, String room, LocalDateTime dateOfMeeting, MeetingType meetingType, List<DiscussionPoint> discussionPoints) {
        this.topic = topic;
        this.room = room;
        this.dateOfMeeting = dateOfMeeting;
        this.meetingType = meetingType;
        this.discussionPoints = discussionPoints;
    }
}
