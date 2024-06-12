package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface MemberService extends UserDetailsService {
    List<Member> getAll();
    Member findById(Long id);
    Member register(String email, String password, String repeatPassword, String name, String surname, String institution, PositionType role);
    Member deleteMember(Long id);
    Member editMember(Long id, String name, String surname, String institution, PositionType role);
}
