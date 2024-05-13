package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<Member> getAll() {
        return memberRepository.findAll();
    }
}
