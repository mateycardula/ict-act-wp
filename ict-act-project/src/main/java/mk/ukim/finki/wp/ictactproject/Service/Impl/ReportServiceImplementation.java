package mk.ukim.finki.wp.ictactproject.Service.Impl;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingIsNotFinishedException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MemberDoesNotExist;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.ReportService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ReportServiceImplementation implements ReportService {
    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;

    public ReportServiceImplementation(MeetingRepository meetingRepository, MemberRepository memberRepository) {
        this.meetingRepository = meetingRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public byte[] createReportForMeeting(Long id) throws IOException {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
        if(!meeting.isFinished()) {
            throw new MeetingIsNotFinishedException();
        }

        ClassPathResource resource = new ClassPathResource("template.docx");
        XWPFDocument document = new XWPFDocument(resource.getInputStream());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        Map<String, Object> replacements = new HashMap<>();

        String placeholder1 = "<date>";
        String replacement1 = meeting.getDateOfMeeting().format(dateFormatter);
        replacements.put(placeholder1, replacement1);

        String placeholder2 = "<type>";
        String replacement2 = meeting.getMeetingTypeForReport();
        replacements.put(placeholder2, replacement2);

        String placeholder3 = "<hourStarted>";
        String replacement3 = meeting.getDateOfMeeting().format(timeFormatter);
        replacements.put(placeholder3, replacement3);

        String placeholder4 = "<president>";
        Member president = memberRepository.findByRoleEquals(PositionType.PRESIDENT).orElseThrow(MemberDoesNotExist::new);
        String replacement4 = president.getName() + " " + president.getSurname();
        replacements.put(placeholder4, replacement4);

        List<Member> members = meeting.getAttendees();
        String placeholder5 = "<members>";

        StringBuilder sbMembers = new StringBuilder();
        for (int i = 0; i < members.size(); i++) {
            sbMembers.append(i + 1).append(". ").append(members.get(i).getName()).append(" ").append(members.get(i).getSurname()).append("\n");
        }
        String replacement5 = sbMembers.toString();
        replacements.put(placeholder5, replacement5);

        List<DiscussionPoint> discussionPoints = meeting.getDiscussionPoints();
        StringBuilder sbPoints = new StringBuilder();
        String placeholder6 = "<discussionPoints>";
        for (DiscussionPoint discussionPoint : discussionPoints) {
            sbPoints.append(discussionPoint.getTopic()).append("\n");
        }
        String replacement6 = sbPoints.toString();
        replacements.put(placeholder6, replacement6);

        String placeholder7 = "<place>";
        String replacement7 = meeting.getRoom();
        replacements.put(placeholder7, replacement7);

        String placeholder8 = "<discussionPointTitle>";
        replacements.put(placeholder8, discussionPoints);

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, replacements);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.write(baos);
        document.close();

        return baos.toByteArray();
    }

    private static void replaceInParagraph(XWPFParagraph paragraph, Map<String, Object> placeholders) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            String text = run.getText(0);
            if (text != null) {
                for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
                    String placeholder = entry.getKey();
                    Object replacement = entry.getValue();

                    if (text.contains(placeholder)) {
                        if (replacement instanceof List) {
                            List<?> list = (List<?>) replacement;
                            if (list.isEmpty()) {
                                continue;
                            }

                            if (list.get(0) instanceof DiscussionPoint) {
                                StringBuilder replacementText = new StringBuilder();
                                for (Object obj : list) {
                                    DiscussionPoint discussion = (DiscussionPoint) obj;
                                    replacementText.append("<title>").append(discussion.getTopic()).append("</title>\n")
                                            .append("\n")
                                            .append(discussion.getDiscussionForReport())
                                            .append(discussion.getVotesForReport())
                                            .append(discussion.getConformationForReport());
                                }
                                text = text.replace(placeholder, replacementText.toString());

                                String[] lines = text.split("\n");
                                run.setText("", 0);
                                for (int j = 0; j < lines.length; j++) {
                                    if (j > 0) {
                                        run.addCarriageReturn();
                                    }
                                    if (lines[j].startsWith("<title>") && lines[j].endsWith("</title>")) {
                                        String titleText = lines[j].substring(7, lines[j].length() - 8);
                                        run = paragraph.createRun();
                                        paragraph.setIndentationLeft(360);
                                        run.setBold(true);
                                        run.setFontSize(14);
                                        run.setFontFamily("Arial");
                                        run.setText(titleText);
                                    } else if (lines[j].startsWith("<description>") && lines[j].endsWith("</description>")) {
                                        run = paragraph.createRun();
                                        run.setFontSize(12);
                                        run.setFontFamily("Arial");
                                        run.setText(lines[j].substring(13, lines[j].length() - 14));
                                    } else {
                                        run = paragraph.createRun();
                                        run.setFontSize(12);
                                        run.setFontFamily("Arial");
                                        run.setText(lines[j]);
                                    }
                                }
                            }
                        } else if (replacement instanceof String) {
                            text = text.replace(placeholder, (String) replacement);

                            String[] lines = text.split("\n");
                            run.setText(lines[0], 0);
                            for (int j = 1; j < lines.length; j++) {
                                run.addCarriageReturn();
                                run.setText(lines[j]);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
}
