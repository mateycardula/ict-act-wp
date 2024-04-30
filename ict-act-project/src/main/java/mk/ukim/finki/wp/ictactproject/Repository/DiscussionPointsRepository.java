package mk.ukim.finki.wp.ictactproject.Repository;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussionPointsRepository extends JpaRepository<DiscussionPoint, Long> {
}
