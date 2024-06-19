package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.InvalidArgumentsException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.InvalidEmailOrPasswordException;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Member login(String email, String password) {
        System.out.println("LOGIN?");
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        Member user = memberRepository.findByEmail(email).orElseThrow(InvalidEmailOrPasswordException::new);

        String pw = user.getPassword();
        if (passwordEncoder.matches(password, pw)) {
            return user;
        }

        return null;

//        return memberRepository.findByEmailAndPassword(email, password)
//                .orElseThrow(InvalidUserCredentialsException::new);
    }
}
