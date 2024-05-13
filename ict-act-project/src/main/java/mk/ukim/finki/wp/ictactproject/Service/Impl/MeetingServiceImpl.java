package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.DiscussionPointDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final DiscussionPointsRepository discussionPointsRepository;
    private final MemberRepository memberRepository;

    public MeetingServiceImpl(MeetingRepository meetingRepository, DiscussionPointsRepository discussionPointsRepository, MemberRepository memberRepository) {
        this.meetingRepository = meetingRepository;
        this.discussionPointsRepository = discussionPointsRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public List<Meeting> listAll() {
        return meetingRepository.findAll();
    }

    // TODO: Discussion points treba da e lista od IDs
    @Override
    public Meeting create(String topic, String room, LocalDateTime dateAndTime, MeetingType meetingType) {
        List<DiscussionPoint> discussionPointList = new ArrayList<>();

//        for (String dp: discussionPoints) {
//            DiscussionPoint newDiscussionPoint = new DiscussionPoint(dp);
//            discussionPointsRepository.save(newDiscussionPoint);
//            discussionPointList.add(newDiscussionPoint);
//        }
        Meeting meeting = new Meeting(topic, room, dateAndTime, meetingType, discussionPointList);
        return meetingRepository.save(meeting);
    }

    @Override
    public Meeting findMeetingById(Long id) {
        return meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
    }

    @Override
    public Meeting addDiscussionPoint(DiscussionPoint discussionPoint, Meeting meeting) {
        List<DiscussionPoint> discussionPointList = meeting.getDiscussionPoints();
        discussionPointList.add(discussionPoint);
        return meetingRepository.save(meeting);
    }

    @Override
    public Meeting findMeetingByDiscussionPoint(Long discussionPointId) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(discussionPointId).orElseThrow(DiscussionPointDoesNotExist::new);
        return meetingRepository.findMeetingByDiscussionPointsContains(discussionPoint);
    }

    @Override
    public Map<Long, List<Member>> getMembersVotedYes(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();
        Map<Long, List<Member>> mapOfVoters = new HashMap<>();
        for (DiscussionPoint discussionPoint : discussionPoints) {
            Long discussionPointId = discussionPoint.getId();
            List<Member> membersVotedYes = discussionPoint.getVotesYes();
            mapOfVoters.put(discussionPointId, membersVotedYes);
        }

        return mapOfVoters;
    }

    @Override
    public Map<Long, List<Member>> getMembersVotedNo(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();
        Map<Long, List<Member>> mapOfVoters = new HashMap<>();
        for (DiscussionPoint discussionPoint : discussionPoints) {
            Long discussionPointId = discussionPoint.getId();
            List<Member> membersVotedNo = discussionPoint.getVotesNo();
            mapOfVoters.put(discussionPointId, membersVotedNo);
        }

        return mapOfVoters;
    }

    @Override
    public Map<Long, String> getDiscussions(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();
        Map<Long, String> mapOfDiscussions = new HashMap<>();
        for (DiscussionPoint discussionPoint : discussionPoints) {
            Long discussionPointId = discussionPoint.getId();
            String discussion = discussionPoint.getDiscussion();
            mapOfDiscussions.put(discussionPointId, discussion);
        }

        return mapOfDiscussions;
    }

    @Override
    public Map<Long, List<Member>> getAllMembersForMeeting(Long meetingId, Map<Long, List<Member>> membersVotedYes, Map<Long, List<Member>> membersVotedNo) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(MeetingDoesNotExistException::new);
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();
        Map<Long, List<Member>> mapOfMembers = new HashMap<>();
        for (DiscussionPoint discussionPoint : discussionPoints) {
            Long discussionPointId = discussionPoint.getId();
            List<Member> membersAlreadyVotedYesForDiscussionPoint = membersVotedYes.get(discussionPointId);
            List<Member> membersAlreadyVotedNoForDiscussionPoint = membersVotedNo.get(discussionPointId);
            List<Member> membersAlreadyVotedForDiscussionPoint = new ArrayList<>();
            membersAlreadyVotedForDiscussionPoint.addAll(membersAlreadyVotedYesForDiscussionPoint);
            membersAlreadyVotedForDiscussionPoint.addAll(membersAlreadyVotedNoForDiscussionPoint);
            List<Member> members = memberRepository.findAll();
            List<Member> membersThatHaveNotVoted = members.stream()
                    .filter(i -> !membersAlreadyVotedForDiscussionPoint.contains(i))
                    .toList();
            mapOfMembers.put(discussionPointId, membersThatHaveNotVoted);
        }

        return mapOfMembers;
    }

    @Override
    public Meeting finishMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(MeetingDoesNotExistException::new);
        List<Member> members = memberRepository.findAll();
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();

        for (DiscussionPoint discussionPoint : discussionPoints) {
            List<Member> membersVotedYes = discussionPoint.getVotesYes();
            List<Member> membersVotedNo = discussionPoint.getVotesNo();
            List<Member> membersAbstained = members.stream()
                    .filter(i -> !membersVotedYes.contains(i))
                    .filter(i -> !membersVotedNo.contains(i))
                    .toList();
            discussionPoint.setAbstained(membersAbstained);

            int votesYes = membersVotedYes.size();
            int votesNo = membersVotedNo.size();
            discussionPoint.setConfirmed(votesYes > votesNo);

//            discussionPointsRepository.save(discussionPoint);
        }

        meeting.setFinished(true);
        return meetingRepository.save(meeting);
    }

    @Override
    public List<Member> getMembersForEditVotes(DiscussionPoint discussionPoint) {
        List<Member> getAllMembers = memberRepository.findAll();
        List<Member> getMembersYes = discussionPoint.getVotesYes();
        List<Member> getMembersNo = discussionPoint.getVotesNo();

        return getAllMembers.stream()
                .filter(i -> !getMembersYes.contains(i))
                .filter(i -> !getMembersNo.contains(i))
                .toList();
    }

    @Override
    public List<Member> getMembersThatVotedYes(Long id) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(id).orElseThrow(DiscussionPointDoesNotExist::new);
        return discussionPoint.getVotesYes();
    }

    @Override
    public List<Member> getMembersThatVotedNo(Long id) {
        DiscussionPoint discussionPoint = discussionPointsRepository.findById(id).orElseThrow(DiscussionPointDoesNotExist::new);
        return discussionPoint.getVotesNo();
    }
}
