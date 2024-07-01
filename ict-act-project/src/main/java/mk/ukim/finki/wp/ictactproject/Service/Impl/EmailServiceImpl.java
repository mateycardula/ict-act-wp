package mk.ukim.finki.wp.ictactproject.Service.Impl;

import jakarta.mail.Address;
import jakarta.mail.SendFailedException;
import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;


import java.util.*;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private final MeetingRepository meetingRepository;

    public EmailServiceImpl(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }


    @Override
    @Async
    @Retryable(value = { MailException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
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

    @Override
    public Map<String, String> getMeetingNotificationPlaceholder(Meeting meeting) {

        Map<String, String> placeholders = new HashMap<>();

        if(meeting.getDraftSubject().isEmpty()  || meeting.getDraftBody().isEmpty()) {
            resetMeetingDraft(meeting);
        }

        placeholders.put("body", meeting.getDraftBody());
        placeholders.put("subject", meeting.getDraftSubject());
        return placeholders;
    }




    @Override
    @Async
    public void sendBatchEmailMeetingNotifications(List<String> mails, Meeting meeting) {
        for (String mail : mails) {
            try {
                sendEmail(mail, meeting.getDraftSubject(), meeting.getDraftBody());
            } catch (MailException e) {
                String errorMessage = "Failed to send email to " + mail + ": " + e.getMessage();
                if (e instanceof MailSendException) {
                    MailSendException sendException = (MailSendException) e;
                    for (Exception nestedException : sendException.getMessageExceptions()) {
                        if (nestedException instanceof SendFailedException) {
                            List<Address> failedToSend = new ArrayList<>();
                            Collections.addAll(failedToSend, ((SendFailedException) nestedException).getInvalidAddresses());
                            Collections.addAll(failedToSend, ((SendFailedException) nestedException).getValidUnsentAddresses());
                            failedToSend.forEach(address -> System.err.println("Failed to send to: " + address));
                        }
                    }
                }
                System.err.println(errorMessage);
            }
        }
    }

    @Override
    public void saveMeetingDraft(String subject, String body, Meeting meeting) {
        meeting.setDraftBody(body);
        meeting.setDraftSubject(subject);
        meetingRepository.save(meeting);

    }

    @Override
    public void resetMeetingDraft(Meeting meeting) {
        meeting.setDraftSubject("Meeting Notification: " + meeting.getTopic() + " - " + meeting.getDateOfMeeting().toLocalDate() + ", " + meeting.getDateOfMeeting().toLocalTime());

        StringBuilder sb = new StringBuilder();

        String body = "Dear Members,\n\n" +
                "I hope this message finds you well.\n\n" +
                "We are scheduled to have a meeting " + meeting.getTopic() + " at " + meeting.getRoom() + " on " + meeting.getDateOfMeeting().toLocalDate() + " at " + meeting.getDateOfMeeting().toLocalTime() + "\n\n" +
                "Please confirm your attendance by responding on the website as soon as possible. Your timely response helps us ensure proper arrangements.\n\n" +
                "During the meeting the following points will be discussed: \n\n";

        sb.append(body);

        for (DiscussionPoint dp : meeting.getDiscussionPoints()) {
            sb.append(dp.getTopic()).append("\n");
        }

        sb.append("\nThank you, and I look forward to seeing you there.\n" +
                "\n" +
                "Best regards");

        meeting.setDraftBody(sb.toString());

        meetingRepository.save(meeting);
    }
}
