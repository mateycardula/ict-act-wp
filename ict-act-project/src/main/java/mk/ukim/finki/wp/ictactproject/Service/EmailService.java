package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.Member;

public interface EmailService {

    public void sendEmail(String to, String subject, String body);

    public void sendVerificationEmail(Member member, String url);
}
