package mk.ukim.finki.wp.ictactproject.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class DiscussionPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    private String discussion;

    @OneToMany
    private List<Member> votesYes;

    @OneToMany
    private List<Member> votesNo;

    @OneToMany
    private List<Member> abstained;

    private boolean confirmed;

    public DiscussionPoint() {
    }

    public DiscussionPoint(String topic) {
        this.topic = topic;
    }
}
