package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class NumberOfVotesExceedsMembersAttendingException extends RuntimeException {
    public NumberOfVotesExceedsMembersAttendingException() {
        super("Number of votes you've entered exceeds the number of members attending this meeting.");
    }
}
