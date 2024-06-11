package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.InvalidEmailOrPasswordException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MemberDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.PasswordDoNotMatchException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.UsernameAlreadyExistsException;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Member findById(long id) {
        return memberRepository.findById(id).orElseThrow(MemberDoesNotExist::new);
    }

    @Override
    public List<Member> getAll() {
        return memberRepository.findAll();
    }

    @Override
    public Member register(String email, String password, String repeatPassword, String name, String surname, String institution) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            throw new InvalidEmailOrPasswordException();
        }

        if (!password.equals(repeatPassword)) {
            throw new PasswordDoNotMatchException();
        }

        if (this.memberRepository.findByEmail(email).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        Member member = new Member(email, passwordEncoder.encode(password), name, surname, institution, PositionType.NEW_USER);
        System.out.println(member.getAuthorities());

        return memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
