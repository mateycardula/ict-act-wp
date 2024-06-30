package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class MeetingIsNotFinishedException extends RuntimeException {
    public MeetingIsNotFinishedException() {
        super("You cannot create a report for a meeting that is not finished.");
    }
}
