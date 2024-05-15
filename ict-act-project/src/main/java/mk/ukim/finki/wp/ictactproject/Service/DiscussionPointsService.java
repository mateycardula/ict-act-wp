package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;

import java.util.List;

public interface DiscussionPointsService {
    DiscussionPoint create(String topic, String discussion);
    DiscussionPoint voteYes(Long[] memberIds, Long discussionPointId);
    DiscussionPoint voteNo(Long[] memberIds, Long discussionPointId);
    DiscussionPoint addDiscussion(String discussion, Long discussionPointId);
    DiscussionPoint getDiscussionPointById(Long id);
    DiscussionPoint editVoteNo(Long[] memberIds, Long id);
    DiscussionPoint editVoteYes(Long[] memberIds, Long id);
    DiscussionPoint deleteVotesYes(Long id);
    DiscussionPoint deleteVotesNo(Long id);
    void deleteDiscussion(Long id);
}
