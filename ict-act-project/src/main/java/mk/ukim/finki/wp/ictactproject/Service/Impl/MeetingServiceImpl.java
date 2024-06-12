package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.DiscussionPointDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MemberDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
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
    public Map<Long, Long> getVotesYes(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();
        Map<Long, Long> mapOfVotes = new HashMap<>();
        for (DiscussionPoint discussionPoint : discussionPoints) {
            Long discussionPointId = discussionPoint.getId();
            Long votesYes = discussionPoint.getVotesYes();
            mapOfVotes.put(discussionPointId, votesYes);
        }

        return mapOfVotes;
    }

    @Override
    public Map<Long, Long> getVotesNo(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();
        Map<Long, Long> mapOfVotes = new HashMap<>();
        for (DiscussionPoint discussionPoint : discussionPoints) {
            Long discussionPointId = discussionPoint.getId();
            Long votesNo = discussionPoint.getVotesNo();
            mapOfVotes.put(discussionPointId, votesNo);
        }

        return mapOfVotes;
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
    public Meeting finishMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(MeetingDoesNotExistException::new);
        Long members = (long) memberRepository.findAll().size();
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();

        for (DiscussionPoint discussionPoint : discussionPoints) {
            Long membersVotedYes = discussionPoint.getVotesYes();
            if(membersVotedYes == null || membersVotedYes < 0L) {
                membersVotedYes = 0L;
                discussionPoint.setVotesYes(membersVotedYes);
            }
            Long membersVotedNo = discussionPoint.getVotesNo();
            if(membersVotedNo == null || membersVotedNo < 0L) {
                membersVotedNo = 0L;
                discussionPoint.setVotesNo(membersVotedNo);
            }
            Long membersAbstained = members - membersVotedNo - membersVotedYes;
            if(membersAbstained < 0L) {
                membersAbstained = 0L;
            }
            discussionPoint.setAbstained(membersAbstained);

            discussionPoint.setConfirmed(membersVotedYes > membersVotedNo);
        }

        meeting.setFinished(true);
        return meetingRepository.save(meeting);
    }

    @Override
    public List<Meeting> filter(String name, LocalDateTime dateFrom, LocalDateTime dateTo, List<MeetingType> type) {
        Set<Meeting> meetings = new HashSet<>(meetingRepository.findAll());
        Set<Meeting> dateFilterFrom = new HashSet<>();
        Set<Meeting> dateFilterTo = new HashSet<>();
        Set<Meeting> topicFilter = new HashSet<>();
        Set<Meeting> typeFilter = new HashSet<>();
        if (dateFrom != null) {
            dateFilterFrom.addAll(meetingRepository.findByDateOfMeetingAfter(dateFrom));
            meetings.retainAll(dateFilterFrom);
        }

        if (dateTo != null) {
            dateFilterTo.addAll(meetingRepository.findByDateOfMeetingBefore(dateTo));
            meetings.retainAll(dateFilterTo);
        }

        if (name != null && !name.isEmpty()) {
            topicFilter.addAll(meetingRepository.findAllByTopicContains(name));
            meetings.retainAll(topicFilter);
        }

        if (type != null) {
            typeFilter.addAll(meetingRepository.findByMeetingTypeIn(type));
            meetings.retainAll(typeFilter);
        }

        return meetings.stream().toList();

    }

    @Override
    public void deleteMeeting(Long id) {
        meetingRepository.deleteById(id);
    }

    @Override
    public Meeting editMeeting(Long id, String topic, String room, LocalDateTime dateAndTime, MeetingType type) {
        Meeting meeting = this.findMeetingById(id);
        meeting.setTopic(topic);
        meeting.setRoom(room);
        meeting.setDateOfMeeting(dateAndTime);
        meeting.setMeetingType(type);
        return meetingRepository.save(meeting);
    }

    @Override
    public Meeting userAttendMeeting(String username, Long id) {
        Meeting meeting = this.findMeetingById(id);
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberDoesNotExist::new);
        List<Member> attendees = meeting.getAttendees();
        if(!attendees.contains(member)) {
            attendees.add(member);
        }
        return meetingRepository.save(meeting);
    }

    @Override
    public List<Long> getMeetingsUserCheckedAttended(String username) {
        List<Meeting> meetings = meetingRepository.findAll();
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberDoesNotExist::new);

        return meetings.stream()
                .filter(i -> i.getAttendees().contains(member))
                .map(Meeting::getId)
                .toList();
    }

    @Override
    public Meeting userCancelAttendance(String username, Long id) {
        Meeting meeting = this.findMeetingById(id);
        Member member = memberRepository.findByEmail(username).orElseThrow(MemberDoesNotExist::new);
        List<Member> attendees = meeting.getAttendees();
        attendees.remove(member);
        return meetingRepository.save(meeting);    }
}
