package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;

import java.util.List;

public interface DiscussionPointsService {
    DiscussionPoint create(String topic, String discussion);
    DiscussionPoint voteYes(Long votes, Long discussionPointId);
    DiscussionPoint voteNo(Long votes, Long discussionPointId);
    DiscussionPoint addDiscussion(String discussion, Long discussionPointId);
    DiscussionPoint getDiscussionPointById(Long id);
    DiscussionPoint deleteVotesYes(Long id);
    DiscussionPoint deleteVotesNo(Long id);

    void editDiscussion(Meeting meeting, Long discussionPointId, String discussion);
    void deleteDiscussion(Meeting meeting, Long dpId);
}
