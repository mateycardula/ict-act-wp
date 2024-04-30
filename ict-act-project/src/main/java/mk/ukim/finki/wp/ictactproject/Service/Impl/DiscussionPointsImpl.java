package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Repository.DiscussionPointsRepository;
import mk.ukim.finki.wp.ictactproject.Service.DiscussionPointsService;
import org.springframework.stereotype.Service;

@Service
public class DiscussionPointsImpl implements DiscussionPointsService {
    private final DiscussionPointsRepository discussionPointsRepository;

    public DiscussionPointsImpl(DiscussionPointsRepository discussionPointsRepository) {
        this.discussionPointsRepository = discussionPointsRepository;
    }

    @Override
    public DiscussionPoint create(String topic) {
        return discussionPointsRepository.save(new DiscussionPoint(topic));
    }
}
