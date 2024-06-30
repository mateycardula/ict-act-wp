package mk.ukim.finki.wp.ictactproject.Web;

import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingDoesNotExistException;
import mk.ukim.finki.wp.ictactproject.Models.exceptions.MeetingIsNotFinishedException;
import mk.ukim.finki.wp.ictactproject.Service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


import java.io.IOException;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate/{id}")
    public ResponseEntity<?> getReport(@PathVariable Long id) throws IOException {
        byte[] documentBytes;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "report-" + id + ".docx");

        try {
            documentBytes = reportService.createReportForMeeting(id);
        } catch (MeetingDoesNotExistException | MeetingIsNotFinishedException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(documentBytes);
    }
}
