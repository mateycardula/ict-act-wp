package mk.ukim.finki.wp.ictactproject.Repository;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.MeetingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Meeting findMeetingByDiscussionPointsContains(DiscussionPoint discussionPoint);

    List<Meeting> findByDateOfMeetingAfter(LocalDateTime dateFrom);

    List<Meeting> findByDateOfMeetingBefore(LocalDateTime dateTo);

    List<Meeting> findAllByTopicContains(String topic);

    List<Meeting> findByMeetingTypeIn(List<MeetingType> types);
}
