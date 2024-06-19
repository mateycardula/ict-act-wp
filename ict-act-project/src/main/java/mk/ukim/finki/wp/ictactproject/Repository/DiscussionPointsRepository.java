package mk.ukim.finki.wp.ictactproject.Repository;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DiscussionPointsRepository extends JpaRepository<DiscussionPoint, Long> {
    @Query("SELECT d.attachment FROM DiscussionPoint d WHERE d.attachment.id = :id")
    Attachment findAttachmentById(@Param("id") UUID id);
}
