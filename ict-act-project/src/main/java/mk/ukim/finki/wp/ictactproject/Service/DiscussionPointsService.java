package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;

public interface DiscussionPointsService {
    DiscussionPoint create(String topic, String discussion);
}
