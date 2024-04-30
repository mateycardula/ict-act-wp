package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import mk.ukim.finki.wp.ictactproject.Service.MeetingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final DiscussionPointsRepository discussionPointsRepository;
    private final DiscussionPointsService discussionPointsService;

    public MeetingServiceImpl(MeetingRepository meetingRepository, DiscussionPointsRepository discussionPointsRepository, DiscussionPointsService discussionPointsService) {
        this.meetingRepository = meetingRepository;
        this.discussionPointsRepository = discussionPointsRepository;
        this.discussionPointsService = discussionPointsService;
    }

    @Override
    public List<Meeting> listAll() {
        return meetingRepository.findAll();
    }

    // TODO: Discussion points treba da e lista od IDs
    @Override
    public Meeting create(String topic, String room, LocalDateTime dateAndTime, MeetingType meetingType, List<String> discussionPoints) {
        List<DiscussionPoint> discussionPointList = new ArrayList<>();

        for (String dp: discussionPoints) {
            DiscussionPoint newDiscussionPoint = new DiscussionPoint(dp);
            discussionPointsRepository.save(newDiscussionPoint);
            discussionPointList.add(newDiscussionPoint);
        }
        Meeting meeting = new Meeting(topic, room, dateAndTime, meetingType, discussionPointList);
        return meetingRepository.save(meeting);
    }
}
