package mk.ukim.finki.wp.ictactproject.Service.Impl;

import com.lowagie.text.*;
//import com.lowagie.text.List;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;
import mk.ukim.finki.wp.ictactproject.Models.Meeting;
import mk.ukim.finki.wp.ictactproject.Models.Member;
import mk.ukim.finki.wp.ictactproject.Models.PositionType;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Repository.MeetingRepository;
import mk.ukim.finki.wp.ictactproject.Repository.MemberRepository;
import mk.ukim.finki.wp.ictactproject.Service.PdfService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Service;



import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;




import java.io.ByteArrayOutputStream;

@Service
public class PdfServiceImpl implements PdfService {
    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;

    public PdfServiceImpl(MeetingRepository meetingRepository, MemberRepository memberRepository) {
        this.meetingRepository = meetingRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public ByteArrayOutputStream generatePdfForMeeting(Long id) throws IOException {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(MeetingDoesNotExistException::new);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        List<Member> attendees = meeting.getAttendees();
        Member president = memberRepository.findByRoleEquals(PositionType.PRESIDENT);

        Font font = FontFactory.getFont("Comic Sans MS", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);


        Document document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph paragraph = new Paragraph("Записник", font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph("од седница на Управниот одбор на ИКТ-АКТ", font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("На ден " + meeting.getDateOfMeeting().format(dateFormatter) + " од " + meeting.getDateOfMeeting().format(timeFormatter) + " часот се одржа седница на Управниот одбор на Асоцијацијата ИКТ-АКТ во просторијата " + meeting.getRoom() + "."));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("На состанокот присуствуваа:"));
            for (Member attendee : attendees) {
                String nameAndSurname = attendee.getName() + " " + attendee.getSurname();
                document.add(new Paragraph(nameAndSurname));
            }
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Дневен ред:"));
            for (DiscussionPoint item : meeting.getDiscussionPoints()) {
                document.add(new Paragraph(item.getTopic()));
            }
            document.add(new Paragraph("\n"));

            for (DiscussionPoint item : meeting.getDiscussionPoints()) {
                document.add(new Paragraph(item.getTopic())); //bold
                document.add(new Paragraph(item.getDiscussion()));
                document.add(new Paragraph(item.getConformation()));
                document.add(new Paragraph("\n"));
            }
            document.add(new Paragraph("\n"));

            // Signatures
            document.add(new Paragraph("Претседавач:"));
            document.add(new Paragraph(president.getName() + " " + president.getSurname()));

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Датум:"));
            document.add(new Paragraph(meeting.getDateOfMeeting().format(dateFormatter)));
            
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return baos;
    }

//    private static void replaceText(PDDocument document, String searchString, String replacement) throws IOException {
//        PDFTextStripper stripper = new PDFTextStripper() {
//            @Override
//            protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
//                for (TextPosition text : textPositions) {
//                    if (string.contains(searchString)) {
//                        PDPage page = document.getPage(getCurrentPageNo() - 1);
//                        PDRectangle mediaBox = page.getMediaBox();
//                        float x = text.getXDirAdj();
//                        float y = mediaBox.getHeight() - text.getYDirAdj();
//                        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
//                        contentStream.beginText();
//                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, text.getFontSize());
//                        contentStream.newLineAtOffset(x, y);
//                        contentStream.showText(replacement);
//                        contentStream.endText();
//                        contentStream.close();
//                    }
//                }
//            }
//        };
//        stripper.getText(document);
//    }

}
