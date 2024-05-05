package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscussionPointsImpl implements DiscussionPointsService {
    private final DiscussionPointsRepository discussionPointsRepository;

    public DiscussionPointsImpl(DiscussionPointsRepository discussionPointsRepository) {
        this.discussionPointsRepository = discussionPointsRepository;
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
}
