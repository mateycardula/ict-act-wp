package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;

import java.util.List;

public interface DiscussionPointsService {
    DiscussionPoint create(String topic, String discussion);
    DiscussionPoint voteYes(Long[] memberIds, Long discussionPointId);
    DiscussionPoint voteNo(Long[] memberIds, Long discussionPointId);
    DiscussionPoint addDiscussion(String discussion, Long discussionPointId);
}
