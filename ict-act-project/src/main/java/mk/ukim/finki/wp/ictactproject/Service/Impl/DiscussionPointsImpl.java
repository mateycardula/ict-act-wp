package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.DiscussionPointDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MemberDoesNotExist;
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
        List<Member> votesYes = new ArrayList<>();
        List<Member> votesNo = new ArrayList<>();
        List<Member> votesAbstained = new ArrayList<>();
        Boolean confirmed = false;

        DiscussionPoint discussionPoint = new DiscussionPoint(topic, discussion, votesYes, votesNo, votesAbstained, confirmed);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint voteYes(Long[] memberIds, Long discussionPointId) {
        List<Long> memberIdsList = Arrays.asList(memberIds);
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(discussionPointId).orElseThrow(DiscussionPointDoesNotExist::new);
        List<Member> members = memberIdsList.stream()
                .map(i -> memberRepository.findById(i).orElseThrow(MemberDoesNotExist::new))
                .toList();
        discussionPoint.getVotesYes().addAll(members);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint voteNo(Long[] memberIds, Long discussionPointId) {
        List<Long> memberIdsList = Arrays.asList(memberIds);
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(discussionPointId).orElseThrow(DiscussionPointDoesNotExist::new);
        List<Member> members = memberIdsList.stream()
                .map(i -> memberRepository.findById(i).orElseThrow(MemberDoesNotExist::new))
                .toList();
        discussionPoint.getVotesNo().addAll(members);
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
    public DiscussionPoint editVoteNo(Long[] memberIds, Long id) {
        List<Long> memberIdsList = Arrays.asList(memberIds);
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(id).orElseThrow(DiscussionPointDoesNotExist::new);
        List<Member> members = memberIdsList.stream()
                .map(i -> memberRepository.findById(i).orElseThrow(MemberDoesNotExist::new))
                .toList();
        discussionPoint.setVotesNo(new ArrayList<>());
        discussionPoint.getVotesNo().addAll(members);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint editVoteYes(Long[] memberIds, Long id) {
        List<Long> memberIdsList = Arrays.asList(memberIds);
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(id).orElseThrow(DiscussionPointDoesNotExist::new);
        List<Member> members = memberIdsList.stream()
                .map(i -> memberRepository.findById(i).orElseThrow(MemberDoesNotExist::new))
                .toList();
        discussionPoint.setVotesYes(new ArrayList<>());
        discussionPoint.getVotesYes().addAll(members);
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint deleteVotesYes(Long id) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(id)
                .orElseThrow(DiscussionPointDoesNotExist::new);
        discussionPoint.setVotesYes(new ArrayList<>());
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public DiscussionPoint deleteVotesNo(Long id) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(id)
                .orElseThrow(DiscussionPointDoesNotExist::new);
        discussionPoint.setVotesNo(new ArrayList<>());
        return discussionPointsRepository.save(discussionPoint);
    }

    @Override
    public void editDiscussion(Meeting meeting, Long discussionPointId, String discussion) {
        DiscussionPoint dp = meeting.getDiscussionPoints()
                .stream().filter(item -> item.getId().equals(discussionPointId))
                .findFirst()
                .orElseThrow(DiscussionPointDoesNotExist::new);

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
