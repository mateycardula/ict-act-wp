package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class MeetingDoesNotExistException extends RuntimeException{
    public MeetingDoesNotExistException() {
        super("The meeting doesn't exist.");
    }
}
