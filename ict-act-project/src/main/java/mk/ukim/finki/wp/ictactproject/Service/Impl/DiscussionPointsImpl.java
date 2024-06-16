package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.*;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public DiscussionPoint create(String topic, String discussion) {
        boolean confirmed = false;

        DiscussionPoint discussionPoint = new DiscussionPoint(topic, discussion, null, null, null, confirmed);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint voteYes(Long votes, Long discussionPointId) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(discussionPointId).orElseThrow(DiscussionPointDoesNotExist::new);
        Meeting meeting = meetingRepository.findMeetingByDiscussionPointsContains(discussionPoint);
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

        if(discussionPoint.getVotesNo() != null) {
            long remainingMembers = membersNumber - discussionPoint.getVotesNo();
            if(votes > remainingMembers) {
                throw new NumberOfVotesExceedsRemainingMembers();
            }
        }

        discussionPoint.setVotesYes(votes);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint voteNo(Long votes, Long discussionPointId) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(discussionPointId).orElseThrow(DiscussionPointDoesNotExist::new);
        Meeting meeting = meetingRepository.findMeetingByDiscussionPointsContains(discussionPoint);
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

        if(discussionPoint.getVotesYes() != null) {
            long remainingMembers = membersNumber - discussionPoint.getVotesYes();
            if (votes > remainingMembers) {
                throw new NumberOfVotesExceedsRemainingMembers();
            }
        }

        discussionPoint.setVotesNo(votes);
        return discussionPointsRepository.save(discussionPoint);
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
    public void editDiscussion(Meeting meeting, Long discussionPointId, String discussion) {
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
}
