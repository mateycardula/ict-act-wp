package mk.ukim.finki.wp.ictactproject.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface PdfService {
    ByteArrayOutputStream generatePdfForMeeting(Long id) throws IOException;
}
