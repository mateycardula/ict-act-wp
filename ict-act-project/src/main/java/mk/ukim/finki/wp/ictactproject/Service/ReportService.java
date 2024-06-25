package mk.ukim.finki.wp.ictactproject.Service;

import java.io.IOException;

public interface ReportService {
    byte[] createReportForMeeting(Long id) throws IOException;
}
