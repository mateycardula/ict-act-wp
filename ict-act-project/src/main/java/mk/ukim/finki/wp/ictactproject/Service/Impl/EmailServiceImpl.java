package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;


    @Override
    @Async
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("iktakttest@gmail.com");
        mailSender.send(message);
    }

    @Override
    @Async
    public void sendVerificationEmail(Member member, String url) {
        String template = "Hello [[name]],<br>"
                + "Click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>";

        String verifyUrl = url + "/members/verify?code=" + member.getVerificationCode();
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("iktakttest@gmail.com");
        message.setTo(member.getUsername());
        message.setSubject("IKT-AKT Verification email");

        template.replace("[[name]]", member.getName() + " " + member.getSurname());

        message.setText(template);
        mailSender.send(message);
    }
}
