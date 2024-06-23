package mk.ukim.finki.wp.ictactproject.Service.Impl;

import jakarta.transaction.Transactional;
import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Attachment;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.*;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class DiscussionPointsImpl implements DiscussionPointsService {
    private final DiscussionPointsRepository discussionPointsRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;

    public DiscussionPointsImpl(DiscussionPointsRepository discussionPointsRepository, MemberRepository memberRepository, MeetingRepository meetingRepository) {
        this.discussionPointsRepository = discussionPointsRepository;
        this.memberRepository = memberRepository;
        this.meetingRepository = meetingRepository;
    }

    @Override
    public DiscussionPoint create(String topic, String discussion, boolean isVotable) {
        boolean confirmed = !isVotable;
        DiscussionPoint discussionPoint = new DiscussionPoint(topic, discussion, null, null, null, confirmed, isVotable);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint voteYes(Long votes, Long discussionPointId) {
        DiscussionPoint validatedDiscussionPointForVote = validateVotes(discussionPointId, votes, "YES");
        validatedDiscussionPointForVote.setVotesYes(votes);
        return discussionPointsRepository.save(validatedDiscussionPointForVote);
    }

    @Override
    public DiscussionPoint voteNo(Long votes, Long discussionPointId) {
        DiscussionPoint validatedDiscussionPointForVote = validateVotes(discussionPointId, votes, "NO");
        validatedDiscussionPointForVote.setVotesNo(votes);
        return discussionPointsRepository.save(validatedDiscussionPointForVote);
    }

    @Override
    public DiscussionPoint addDiscussion(String discussion, Long discussionPointId) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(discussionPointId).orElseThrow(DiscussionPointDoesNotExist::new);
        discussionPoint.setDiscussion(discussion);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint getDiscussionPointById(Long id) {
        return discussionPointsRepository.findById(id).orElseThrow(DiscussionPointDoesNotExist::new);
    }

    @Override
    public DiscussionPoint deleteVotesYes(Long id) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(id)
                .orElseThrow(DiscussionPointDoesNotExist::new);
        discussionPoint.setVotesYes(null);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint deleteVotesNo(Long id) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(id)
                .orElseThrow(DiscussionPointDoesNotExist::new);
        discussionPoint.setVotesNo(null);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public Meeting getParentMeetingByDiscussionPointId(Long discussionPointId) {
        DiscussionPoint discussionPoint = getDiscussionPointById(discussionPointId);
        return meetingRepository.findMeetingByDiscussionPointsContains(discussionPoint);
    }

    @Override
    public void editDiscussion(Long discussionPointId, String discussion) {
        DiscussionPoint dp = discussionPointsRepository.findById(discussionPointId).orElseThrow(DiscussionPointDoesNotExist::new);
        dp.setDiscussion(discussion);
        discussionPointsRepository.save(dp);
    }

    @Override
    public void deleteDiscussion(Meeting meeting, Long dpId) {
        DiscussionPoint dp = discussionPointsRepository.findById(dpId)
                .orElseThrow(DiscussionPointDoesNotExist::new);
        meeting.getDiscussionPoints().remove(dp);

        meetingRepository.save(meeting);
        discussionPointsRepository.delete(dp);
    }

    @Override
    public DiscussionPoint validateVotes(Long discussionPointId, Long votes, String voteType) {
        Meeting meeting = getParentMeetingByDiscussionPointId(discussionPointId);
        DiscussionPoint discussionPoint = getDiscussionPointById(discussionPointId);

        if(!discussionPoint.isVotable()) throw new DiscussionPointNotVotable();

        Integer membersNumber = meeting.getAttendees().size();

        if(votes == null) {
            votes = 0L;
        }

        if(votes < 0) {
            throw new VotesMustBeZeroOrGreaterException();
        }

        if(votes > membersNumber) {
            throw new NumberOfVotesExceedsMembersAttendingException();
        }

        if(Objects.equals(voteType, "NO")){
            if(discussionPoint.getVotesYes() != null) {
                long remainingMembers = membersNumber - discussionPoint.getVotesYes();
                if (votes > remainingMembers) {
                    throw new NumberOfVotesExceedsRemainingMembers();
                }
            }
        }
        else{
            if(discussionPoint.getVotesNo() != null) {
                long remainingMembers = membersNumber - discussionPoint.getVotesNo();
                if(votes > remainingMembers) {
                    throw new NumberOfVotesExceedsRemainingMembers();
                }
            }
        }

        return discussionPoint;
    }

    @Transactional
    @Override
    public DiscussionPoint editDiscussionPoint(Long discussionPointId, String topic,
                                               String discussion, boolean isVotable,
                                               MultipartFile file, boolean removeAttachment) throws Exception {
        DiscussionPoint pointToEdit = getDiscussionPointById(discussionPointId);
        pointToEdit.setTopic(topic);
        pointToEdit.setDiscussion(discussion);
        pointToEdit.setVotable(isVotable);

        UUID deleteAttachmentUuid = null;

        try {
            if (!file.isEmpty()) {
                Attachment attachment = new Attachment(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                );
                pointToEdit.setAttachment(attachment);
            } else if (removeAttachment && pointToEdit.getAttachment() != null) {
                deleteAttachmentUuid = pointToEdit.getAttachment().getId();
                pointToEdit.setAttachment(null);
            }

            DiscussionPoint savedDP = discussionPointsRepository.saveAndFlush(pointToEdit);

            if (deleteAttachmentUuid != null) {
                discussionPointsRepository.deleteAttachmentById(deleteAttachmentUuid);
            }

            return savedDP;

        } catch (Exception e) {
            throw new Exception("File handling error", e);
        }
    }
    @Override
    public Attachment getAttachmentById(UUID id) {
        return discussionPointsRepository.findAttachmentById(id);
    }
}
