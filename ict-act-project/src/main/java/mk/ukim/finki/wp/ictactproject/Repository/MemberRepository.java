package mk.ukim.finki.wp.ictactproject.Repository;

import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndPassword(String email, String password);
    Optional<Member> deleteByEmail(String email);
    Optional<Member> findById(Long id);

    @Query("SELECT m.email FROM Member m WHERE m.role IN :positionTypesList")
    List<String> findEmailsByRoleIn(List<PositionType> positionTypesList);


    Optional<Member> findByRoleEquals(PositionType positionType);
}
