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

    private LocalDateTime dateAndTime;

    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @OneToMany
    private List<DiscussionPoint> discussionPoints;

    @OneToOne
    private MeetingReport meetingReport;
}
