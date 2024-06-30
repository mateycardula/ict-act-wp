package mk.ukim.finki.wp.ictactproject.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    private List<DiscussionPoint> discussionPoints = new ArrayList<>();

    @OneToOne
    private MeetingReport meetingReport;

    private boolean finished = false;
    @ManyToMany
    private List<Member> attendees;

    // maybe, no
    @ManyToMany
    private List<Member> maybeAttendees;

    @ManyToMany
    private List<Member> noAttendees;

    @ManyToMany
    private List<Member> registeredAttendees;

    public Meeting() {}

    public Meeting(String topic, String room, LocalDateTime dateOfMeeting, MeetingType meetingType, List<DiscussionPoint> discussionPoints) {
        this.topic = topic;
        this.room = room;
        this.dateOfMeeting = dateOfMeeting;
        this.meetingType = meetingType;
        this.discussionPoints = discussionPoints;
        this.attendees = new ArrayList<>();
    }

    public String getMeetingTypeForReport() {
        if(meetingType.equals(MeetingType.BOARD_MEETING)) {
            return "управниот одбор";
        } else if(meetingType.equals(MeetingType.GENERAL_ASSEMBLY)) {
            return "генералното собрание";
        } else {
            return "членовите";
        }
    }

    public static final Comparator<Meeting> COMPARATOR = Comparator
            .comparing(Meeting::getDateOfMeeting)
            .thenComparing(meeting ->  meeting.getDateOfMeeting().toLocalTime())
            .thenComparing(Meeting::getTopic);
}
