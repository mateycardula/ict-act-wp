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

    @ManyToMany
    private List<Member> votesYes;

    @ManyToMany
    private List<Member> votesNo;

    @ManyToMany
    private List<Member> abstained;

    private boolean confirmed;
}
