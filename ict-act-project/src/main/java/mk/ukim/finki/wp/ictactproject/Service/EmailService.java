package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;

import java.util.List;
import java.util.Map;

public interface EmailService {

    public void sendEmail(String to, String subject, String body);

    public void sendVerificationEmail(Member member, String url);

    public Map<String, String> getMeetingNotificationPlaceholder(Meeting meeting);

    public void sendBatchMail(List<String> mails, String subject, String body, Meeting meeting);

    public void saveMeetingDraft(String subject, String body,  Meeting meeting);
    public void resetMeetingDraft(Meeting meeting);
}
