package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class VotesMustBeZeroOrGreaterException extends RuntimeException {
    public VotesMustBeZeroOrGreaterException() {
        super("Please enter a number equal or greater than zero.");
    }
}
