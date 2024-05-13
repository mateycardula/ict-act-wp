package mk.ukim.finki.wp.ictactproject.Repository;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Meeting findMeetingByDiscussionPointsContains(DiscussionPoint discussionPoint);
}
