package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.*;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.DiscussionPointDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MemberDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        discussionPoint.setTopic((discussionPointList.size() + ". " + discussionPoint.getTopic()));
        discussionPointsRepository.save(discussionPoint);
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
    public Map<Long, Long> getVotesAbstained(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();
        Map<Long, Long> mapOfVotes = new HashMap<>();
        for (DiscussionPoint discussionPoint : discussionPoints) {
            Long discussionPointId = discussionPoint.getId();
            Long votesAbstained = discussionPoint.getAbstained();
            mapOfVotes.put(discussionPointId, votesAbstained);
        }

        return mapOfVotes;
    }

    @Override
    public Map<Long, String> getDiscussions(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints().stream().sorted(DiscussionPoint.SORT_BY_TOPIC).toList();

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
        Long members = (long) meeting.getAttendees().size();
        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();

        for (DiscussionPoint discussionPoint : discussionPoints) {
            if(discussionPoint.isVotable()) {
                Long membersVotedYes = discussionPoint.getVotesYes();
                if (membersVotedYes == null || membersVotedYes < 0L) {
                    membersVotedYes = 0L;
                    discussionPoint.setVotesYes(membersVotedYes);
                }
                Long membersVotedNo = discussionPoint.getVotesNo();
                if (membersVotedNo == null || membersVotedNo < 0L) {
                    membersVotedNo = 0L;
                    discussionPoint.setVotesNo(membersVotedNo);
                }

                Long membersAbstained = discussionPoint.getAbstained();
                if (membersAbstained == null || membersAbstained < 0L) {
                    membersAbstained = 0L;
                    discussionPoint.setAbstained(membersAbstained);
                }

                boolean isConfirmed = membersVotedYes > (membersVotedNo + membersAbstained);

                discussionPoint.setConfirmed(isConfirmed);

            }
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

        return meetings.stream()
                .sorted(Meeting.COMPARATOR)
                .toList();

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

//    @Override
//    public Meeting changeLoggedUserAttendanceStatus(Long meetingId) {
//
//        Meeting meeting = this.findMeetingById(meetingId);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = null;
//
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            username = userDetails.getUsername();
//        }
//
//        Member member = memberRepository.findByEmail(username).orElseThrow(MemberDoesNotExist::new);
//        List<Member> possibleAttendees = meeting.getRegisteredAttendees();
//
//        if(possibleAttendees.contains(member)) {
//            possibleAttendees.remove(member);
//        }
//        else possibleAttendees.add(member);
//
//        return meetingRepository.save(meeting);
//    }

    @Override
    public Meeting changeLoggedUserAttendanceStatus(Long meetingId, AttendanceStatus status) {

        Meeting meeting = this.findMeetingById(meetingId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        Member member = memberRepository.findByEmail(username).orElseThrow(MemberDoesNotExist::new);
        member.setAttendanceStatus(status);

        meeting.getRegisteredAttendees().remove(member);
        meeting.getMaybeAttendees().remove(member);
        meeting.getNoAttendees().remove(member);

        switch (status) {
            case YES:
                meeting.getRegisteredAttendees().add(member);
                break;
            case MAYBE:
                meeting.getMaybeAttendees().add(member);
                break;
            case NO:
                meeting.getNoAttendees().add(member);
                break;
        }

        memberRepository.save(member);
        return meetingRepository.save(meeting);

    }

    @Override
    public List<DiscussionPoint> getDiscussionPointsSorted(Long meetingId) {
        Meeting meeting = findMeetingById(meetingId);
        return meeting.getDiscussionPoints().stream().sorted(DiscussionPoint.SORT_BY_TOPIC).toList();
    }

    @Override
    public List<Long> getMeetingsUserCheckedAttended() {
        List<Long> userAttendanceMeetings = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            Member member = memberRepository.findByEmail(username).orElseThrow(MemberDoesNotExist::new);
            List<Meeting> meetings = meetingRepository.findAll();

            userAttendanceMeetings =  meetings.stream()
                    .filter(i -> i.getRegisteredAttendees().contains(member))
                    .map(Meeting::getId)
                    .toList();
        }

        return userAttendanceMeetings;

    }

    //maybe attendees
    @Override
    public List<Long> getMeetingsUserMaybeCheckedAttended() {
        List<Long> userMaybeAttendanceMeetings = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            Member member = memberRepository.findByEmail(username).orElseThrow(MemberDoesNotExist::new);
            List<Meeting> meetings = meetingRepository.findAll();

            userMaybeAttendanceMeetings =  meetings.stream()
                    .filter(i -> i.getMaybeAttendees().contains(member))
                    .map(Meeting::getId)
                    .toList();
        }

        return userMaybeAttendanceMeetings;

    }

//    no attendees
    @Override
    public List<Long> getMeetingsUserNotCheckedAttended() {
        List<Long> userNoAttendanceMeetings = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            Member member = memberRepository.findByEmail(username).orElseThrow(MemberDoesNotExist::new);
            List<Meeting> meetings = meetingRepository.findAll();

            userNoAttendanceMeetings =  meetings.stream()
                    .filter(i -> i.getNoAttendees().contains(member))
                    .map(Meeting::getId)
                    .toList();
        }

        return userNoAttendanceMeetings;

    }

    @Override
    public void addAttendants(List<Member> updatedAttendees, Long meetingId) {
        Meeting meeting = this.findMeetingById(meetingId);

        Set<Member> attendeesToRemove = new HashSet<>(meeting.getAttendees());
        Set<Member> attendeesToAdd = new HashSet<>(updatedAttendees);

        attendeesToRemove.removeAll(attendeesToAdd);

         for (Member attendant : updatedAttendees) {
            confirmUserAttendance(attendant, meeting);
        }

         for (Member attendant : attendeesToRemove) {
             removeUserAttendance(attendant, meeting);
         }
    }

    @Override
    public void removeUserAttendance(Member member, Meeting meeting) {
        List<Member> attendees = meeting.getAttendees();
        if(attendees.contains(member)) {
            attendees.remove(member);
        }

        meetingRepository.save(meeting);
    }


    @Override
    public Meeting confirmUserAttendance(Member member, Meeting meeting) {
        List<Member> attendees = meeting.getAttendees();
        if(!attendees.contains(member)) {
            attendees.add(member);
        }
        return meetingRepository.save(meeting);
    }




}
