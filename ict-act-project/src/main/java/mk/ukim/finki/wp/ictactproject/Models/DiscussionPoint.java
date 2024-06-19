package mk.ukim.finki.wp.ictactproject.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
public class DiscussionPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Column(length = 999)
    private String discussion;

//    @ManyToMany(cascade = CascadeType.REMOVE)
    private Long votesYes;

//    @ManyToMany(cascade = CascadeType.REMOVE)
    private Long votesNo;

//    @ManyToMany(cascade = CascadeType.REMOVE)
    private Long abstained;

    private boolean confirmed;

    private boolean isVotable;

    public DiscussionPoint() {
    }

    public DiscussionPoint(String topic, String discussion, Long votesYes, Long votesNo, Long abstained, boolean confirmed, boolean isVotable) {
        this.topic = topic;
        this.discussion = discussion;
        this.votesYes = votesYes;
        this.votesNo = votesNo;
        this.abstained = abstained;
        this.confirmed = confirmed;
        this.isVotable = isVotable;
    }

    public DiscussionPoint(String topic) {
        this.topic = topic;
    }

    public static Comparator<DiscussionPoint> SORT_BY_TOPIC = Comparator.
            comparing(DiscussionPoint::getTopic);

    public String getConformation() {
        if(this.confirmed) {
            return "Усвоена";
        }
        else return "Одбиена";
    }
}
