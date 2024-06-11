package mk.ukim.finki.wp.ictactproject.Repository;

import mk.ukim.finki.wp.ictactproject.Models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmailAndPassword(String email, String password);
    Optional<Member> deleteByEmail(String email);
    Optional<Member> findById(Long id);

}
