package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface DiscussionPointsService {
    DiscussionPoint create(String topic, String discussion, boolean isVotable);
    DiscussionPoint voteYes(Long votes, Long discussionPointId);
    DiscussionPoint voteNo(Long votes, Long discussionPointId);
    DiscussionPoint addDiscussion(String discussion, Long discussionPointId);
    DiscussionPoint getDiscussionPointById(Long id);
    DiscussionPoint deleteVotesYes(Long id);
    DiscussionPoint deleteVotesNo(Long id);

    Meeting getParentMeetingByDiscussionPointId(Long discussionPointId);
    void editDiscussion(Long discussionPointId, String discussion);
    void deleteDiscussion(Meeting meeting, Long dpId);
    DiscussionPoint validateVotes(Long discussionPointId, Long votes, String voteType);

    DiscussionPoint editDiscussionPoint(Long discussionPointId, String topic, String discussion, boolean isVotable, MultipartFile file, boolean removeAttachment) throws Exception;

    Attachment getAttachmentById(UUID id);
}
